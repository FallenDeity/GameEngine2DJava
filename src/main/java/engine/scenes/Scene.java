package engine.scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.components.*;
import engine.components.sprites.Sprite;
import engine.physics2d.Physics2D;
import engine.renderer.Camera;
import engine.renderer.Renderer;
import engine.ruby.Window;
import engine.util.AssetPool;
import engine.util.CONSTANTS;
import engine.util.ComponentDeserializer;
import engine.util.GameObjectDeserializer;
import org.joml.Vector2f;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Stream;

public abstract class Scene {
	protected final Renderer renderer = new Renderer();
	protected final List<GameObject> gameObjects = new ArrayList<>();
	protected final Physics2D physics2D;
	protected final Camera camera;
	private final List<GameObject> pendingGameObjects = new ArrayList<>();
	private final String storePath = "%s/scenes".formatted(CONSTANTS.RESOURCE_PATH.getValue());
	private boolean isRunning = false;

	protected Scene() {
		load();
		for (GameObject g : gameObjects) {
			if (g.getComponent(SpriteRenderer.class) != null) {
				Sprite sp = g.getComponent(SpriteRenderer.class).getSprite();
				if (sp.getTexture() != null) {
					sp.setTexture(AssetPool.getTexture(sp.getTexture().getFilePath()));
				}
			}
			if (g.getComponent(StateMachine.class) != null) {
				StateMachine sm = g.getComponent(StateMachine.class);
				sm.refresh();
			}
		}
		camera = new Camera(new Vector2f());
		physics2D = new Physics2D();
	}

	public static GameObject createGameObject(String name) {
		GameObject gameObject = new GameObject(name);
		gameObject.addComponent(new Transform());
		gameObject.transform = gameObject.getComponent(Transform.class);
		return gameObject;
	}

	public final void start() {
		if (isRunning) return;
		isRunning = true;
		for (GameObject gameObject : gameObjects) {
			gameObject.start();
			renderer.add(gameObject);
			physics2D.add(gameObject);
		}
	}	private String defaultScene = Window.getScene() == null ? "default" : Window.getScene().getDefaultScene();

	public final void addGameObjectToScene(GameObject gameObject) {
		if (isRunning) {
			pendingGameObjects.add(gameObject);
		} else {
			gameObjects.add(gameObject);
		}
	}

	public final void destroy() {
		gameObjects.forEach(GameObject::destroy);
	}

	public void update(float dt) {
		camera.adjustProjectionMatrix();
		physics2D.update(dt);
		for (int i = 0; i < gameObjects.size(); i++) {
			GameObject go = gameObjects.get(i);
			go.update(dt);
			if (go.isDestroyed()) {
				gameObjects.remove(i);
				renderer.destroyGameObject(go);
				physics2D.destroyGameObject(go);
				i--;
			}
		}
		for (GameObject gameObject : pendingGameObjects) {
			gameObjects.add(gameObject);
			gameObject.start();
			renderer.add(gameObject);
			physics2D.add(gameObject);
		}
		pendingGameObjects.clear();
	}

	public void editorUpdate(float dt) {
		camera.adjustProjectionMatrix();
		for (int i = 0; i < gameObjects.size(); i++) {
			GameObject go = gameObjects.get(i);
			go.editorUpdate(dt);
			if (go.isDestroyed()) {
				gameObjects.remove(i);
				renderer.destroyGameObject(go);
				physics2D.destroyGameObject(go);
				i--;
			}
		}
		for (GameObject gameObject : pendingGameObjects) {
			gameObjects.add(gameObject);
			gameObject.start();
			renderer.add(gameObject);
			physics2D.add(gameObject);
		}
		pendingGameObjects.clear();
	}

	public void render() {
		renderer.render();
	}

	public final Camera getCamera() {
		return camera;
	}

	public void imGui() {
	}

	public GameObject getGameObject(int uid) {
		return gameObjects.stream().filter(gameObject -> gameObject.getUid() == uid).findFirst().orElse(null);
	}

	public GameObject getGameObject(String name) {
		return gameObjects.stream().filter(gameObject -> gameObject.getName().equals(name)).findFirst().orElse(null);
	}

	public <T extends Component> GameObject getGameObject(Class<T> componentClass) {
		return gameObjects.stream().filter(gameObject -> gameObject.getComponent(componentClass) != null).findFirst().orElse(null);
	}

	public List<GameObject> getGameObjects() {
		return gameObjects;
	}

	public Physics2D getPhysics2D() {
		return physics2D;
	}

	public String getDefaultScene() {
		return defaultScene;
	}

	public void setDefaultScene(String defaultScene) {
		this.defaultScene = defaultScene;
	}

	public final void export() {
		GsonBuilder gsonb = new GsonBuilder();
		gsonb.registerTypeAdapter(Component.class, new ComponentDeserializer());
		gsonb.registerTypeAdapter(GameObject.class, new GameObjectDeserializer());
		gsonb.enableComplexMapKeySerialization();
		Gson gson = gsonb.setPrettyPrinting().create();
		try {
			Files.createDirectories(Path.of(storePath));
			String path = "%s/%s.json".formatted(storePath, defaultScene);
			FileWriter writer = new FileWriter(path);
			writer.write(gson.toJson(gameObjects.stream().filter(GameObject::isSerializable).toArray()));
			writer.close();
			System.out.println("Exported scene to: " + path);
		} catch (IOException e) {
			Logger logger = Logger.getLogger(getClass().getSimpleName());
			logger.severe(e.getMessage());
		}
	}

	public final void load() {
		GsonBuilder gsonb = new GsonBuilder();
		gsonb.registerTypeAdapter(Component.class, new ComponentDeserializer());
		gsonb.registerTypeAdapter(GameObject.class, new GameObjectDeserializer());
		gsonb.enableComplexMapKeySerialization();
		Gson gson = gsonb.create();
		try {
			String path = "%s/%s.json".formatted(storePath, defaultScene);
			String json = Objects.requireNonNull(Files.readString(Path.of(path)));
			int gId = -1, cId = -1;
			GameObject[] gameObjects = gson.fromJson(json, GameObject[].class);
			gId = Stream.of(gameObjects).mapToInt(GameObject::getUid).max().orElse(gId);
			for (GameObject gameObject : gameObjects) {
				addGameObjectToScene(gameObject);
				cId = gameObject.getComponents().stream().mapToInt(Component::getUid).max().orElse(cId);
			}
			GameObject.setIdCounter(++gId);
			Component.setIdCounter(++cId);
			System.out.println("Loaded scene from: " + path);
		} catch (Exception e) {
			Logger logger = Logger.getLogger(getClass().getSimpleName());
			logger.severe(e.getMessage());
		}
	}




}
