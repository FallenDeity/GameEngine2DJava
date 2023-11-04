package engine.components;

import engine.editor.PropertiesWindow;
import engine.ruby.KeyListener;
import engine.ruby.Window;
import engine.util.CONSTANTS;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component {
	private float debounce = 0.0f;

	@Override
	public void update(float dt) {

	}

	@Override
	public void editorUpdate(float dt) {
		debounce -= dt;
		PropertiesWindow window = Window.getImGuiLayer().getPropertiesWindow();
		GameObject activeGameObject = window.getActiveGameObject();
		List<GameObject> gameObjects = window.getGameObjects();
		float multiplier = KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT) ? 0.1f : 1.0f;
		if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && KeyListener.keyBeginPress(GLFW_KEY_D) && activeGameObject != null && debounce <= 0.0f) {
			GameObject obj = window.getActiveGameObject().copy();
			obj.transform.setPosition(new Vector2f(obj.transform.getPosition()).add(CONSTANTS.GRID_WIDTH.getIntValue(), 0));
			Window.getScene().addGameObjectToScene(obj);
			window.setActiveGameObject(obj);
			if (obj.getComponent(StateMachine.class) != null) {
				obj.getComponent(StateMachine.class).refresh();
			}
			debounce = 0.2f;
		} else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && KeyListener.isKeyPressed(GLFW_KEY_D) && gameObjects.size() > 1 && debounce <= 0.0f) {
			List<GameObject> selected = new ArrayList<>(gameObjects);
			window.clearGameObjects();
			for (GameObject obj : selected) {
				GameObject copy = obj.copy();
				Window.getScene().addGameObjectToScene(copy);
				window.addActiveGameObject(copy);
				if (copy.getComponent(StateMachine.class) != null) {
					copy.getComponent(StateMachine.class).refresh();
				}
			}
			debounce = 0.2f;
		} else if (KeyListener.isKeyPressed(GLFW_KEY_DELETE)) {
			gameObjects.forEach(GameObject::destroy);
			window.clearGameObjects();
		} else if (KeyListener.isKeyPressed(GLFW_KEY_PAGE_DOWN) && debounce < 0) {
			debounce = 0.2f;
			for (GameObject obj : gameObjects) {
				obj.transform.setZIndex(obj.transform.getZIndex() - 1);
			}
		} else if (KeyListener.isKeyPressed(GLFW_KEY_PAGE_UP) && debounce < 0) {
			debounce = 0.2f;
			for (GameObject obj : gameObjects) {
				obj.transform.setZIndex(obj.transform.getZIndex() + 1);
			}
		} else if (KeyListener.isKeyPressed(GLFW_KEY_UP) && debounce < 0) {
			debounce = 0.2f;
			for (GameObject obj : gameObjects) {
				obj.transform.setPosition(new Vector2f(obj.transform.getPosition()).add(0, CONSTANTS.GRID_WIDTH.getIntValue() * multiplier));
			}
		} else if (KeyListener.isKeyPressed(GLFW_KEY_DOWN) && debounce < 0) {
			debounce = 0.2f;
			for (GameObject obj : gameObjects) {
				obj.transform.setPosition(new Vector2f(obj.transform.getPosition()).add(0, -CONSTANTS.GRID_WIDTH.getIntValue() * multiplier));
			}
		} else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) && debounce < 0) {
			debounce = 0.2f;
			for (GameObject obj : gameObjects) {
				obj.transform.setPosition(new Vector2f(obj.transform.getPosition()).add(-CONSTANTS.GRID_WIDTH.getIntValue() * multiplier, 0));
			}
		} else if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) && debounce < 0) {
			debounce = 0.2f;
			for (GameObject obj : gameObjects) {
				obj.transform.setPosition(new Vector2f(obj.transform.getPosition()).add(CONSTANTS.GRID_WIDTH.getIntValue() * multiplier, 0));
			}
		}
	}

	@Override
	public void start() {

	}
}
