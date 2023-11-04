package engine.components;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.util.AssetPool;
import engine.util.ComponentDeserializer;
import engine.util.GameObjectDeserializer;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GameObject {
	private static int ID_COUNTER = 0;
	private final List<Component> components = new ArrayList<>();
	public transient Transform transform;
	private String name;
	private int uid;
	private boolean serializable = true;
	private boolean isDestroyed = false;

	public GameObject(String name) {
		this.name = name;
		uid = ID_COUNTER++;
	}

	public static void setIdCounter(int id) {
		ID_COUNTER = id;
	}

	public <T extends Component> T getComponent(Class<T> componentClass) {
		for (Component component : components) {
			if (componentClass.isAssignableFrom(component.getClass())) {
				try {
					return componentClass.cast(component);
				} catch (ClassCastException e) {
					Logger logger = Logger.getLogger(name);
					logger.severe(e.getMessage());
					assert false
							: "Error: (GameObject) Could not cast component '" + componentClass.getName() + "'";
				}
			}
		}
		return null;
	}

	public <T extends Component> void removeComponent(Class<T> componentClass) {
		for (int i = 0; i < components.size(); i++) {
			Component component = components.get(i);
			if (componentClass.isAssignableFrom(component.getClass())) {
				components.remove(component);
				return;
			}
		}
	}

	public List<Component> getComponents() {
		return components;
	}

	public void addComponent(Component component) {
		component.generateUid();
		components.add(component);
		component.gameObject = this;
	}

	public void editorUpdate(float dt) {
		components.forEach(component -> component.editorUpdate(dt));
	}

	public void update(float dt) {
		components.forEach(component -> component.update(dt));
	}

	public void start() {
		components.forEach(Component::start);
	}

	public void imGui() {
		components.forEach(component -> {
			if (ImGui.collapsingHeader(component.getClass().getSimpleName())) {
				component.imGui();
			}
		});
	}

	public void destroy() {
		if (isDestroyed) return;
		isDestroyed = true;
		components.forEach(Component::destroy);
	}

	public GameObject copy() {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Component.class, new ComponentDeserializer())
				.registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
				.enableComplexMapKeySerialization()
				.create();
		String json = gson.toJson(this);
		GameObject obj = gson.fromJson(json, GameObject.class);
		obj.generateUid();
		obj.getComponents().forEach(Component::generateUid);
		SpriteRenderer spr = obj.getComponent(SpriteRenderer.class);
		if (spr != null && spr.getTexture() != null) {
			spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilePath()));
		}
		return obj;
	}

	public boolean isDestroyed() {
		return isDestroyed;
	}

	public void generateUid() {
		uid = ID_COUNTER++;
	}

	public int getUid() {
		return uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNotSerializable() {
		serializable = false;
	}

	public boolean isSerializable() {
		return serializable;
	}
}
