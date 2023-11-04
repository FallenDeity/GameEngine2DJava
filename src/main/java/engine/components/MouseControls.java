package engine.components;

import engine.editor.PropertiesWindow;
import engine.renderer.DebugDraw;
import engine.renderer.Picker;
import engine.ruby.ImGuiLayer;
import engine.ruby.KeyListener;
import engine.ruby.MouseListener;
import engine.ruby.Window;
import engine.scenes.LevelEditorScene;
import engine.util.CONSTANTS;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component {
	private static boolean holding = false;
	private final Vector2f boxSelectStart = new Vector2f(), boxSelectEnd = new Vector2f();
	private GameObject activeGameObject = null;
	private float debounce = 0.0f;
	private boolean boxSelect = false;

	public static boolean isHolding() {
		return holding;
	}

	public void setActiveGameObject(GameObject activeGameObject) {
		if (this.activeGameObject != null) {
			this.activeGameObject.destroy();
		}
		this.activeGameObject = activeGameObject;
		this.activeGameObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0.8f, 0.8f, 0.8f, 0.5f));
		this.activeGameObject.addComponent(new NonPickable());
		Window.getScene().addGameObjectToScene(activeGameObject);
	}

	public void placeObject() {
		GameObject obj = activeGameObject.copy();
		if (obj.getComponent(StateMachine.class) != null) {
			obj.getComponent(StateMachine.class).refresh();
		}
		obj.getComponent(SpriteRenderer.class).setColor(new Vector4f(1, 1, 1, 1));
		obj.removeComponent(NonPickable.class);
		Window.getScene().addGameObjectToScene(obj);
	}

	@Override
	public void update(float dt) {

	}

	@Override
	public void editorUpdate(float dt) {
		debounce -= dt;
		Picker picker = Window.getImGuiLayer().getPropertiesWindow().getPicker();
		LevelEditorScene currentScene = (LevelEditorScene) Window.getScene();
		if (activeGameObject != null) {
			holding = true;
			Vector2f pos = MouseListener.getWorld();
			float width = CONSTANTS.GRID_WIDTH.getIntValue(), height = CONSTANTS.GRID_HEIGHT.getIntValue();
			float firstX = (((int) Math.floor(pos.x / width)) * width) + width / 2.0f;
			float firstY = (((int) Math.floor(pos.y / height)) * height) + height / 2.0f;
			activeGameObject.transform.setPosition(new Vector2f(firstX, firstY));
			if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && ImGuiLayer.getWantCaptureMouse()) {
				float halfWidth = width / 2.0f, halfHeight = height / 2.0f;
				if (MouseListener.isDragging() && blockInSquare(activeGameObject.transform.getPosition().x - halfWidth, activeGameObject.transform.getPosition().y - halfHeight)) {
					placeObject();
				} else if (!MouseListener.isDragging() && debounce <= 0 && blockInSquare(activeGameObject.transform.getPosition().x - halfWidth, activeGameObject.transform.getPosition().y - halfHeight)) {
					placeObject();
					debounce = 0.2f;
				}
			}
			if (KeyListener.keyBeginPress(GLFW_KEY_ESCAPE)) {
				activeGameObject.destroy();
				activeGameObject = null;
				holding = false;
			}
		} else if (!isHolding() && !MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && ImGuiLayer.getWantCaptureMouse() && debounce <= 0) {
			int x = (int) MouseListener.getScreenX(), y = (int) MouseListener.getScreenY();
			GameObject obj = currentScene.getGameObject(picker.readPixel(x, y));
			if (obj != null && obj.getComponent(NonPickable.class) == null) {
				Window.getImGuiLayer().getPropertiesWindow().setActiveGameObject(obj);
			} else if (obj == null && !MouseListener.isDragging()) {
				Window.getImGuiLayer().getPropertiesWindow().clearGameObjects();
			}
			debounce = 0.2f;
		} else if (MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && !currentScene.gizmoActive()) {
			if (!boxSelect) {
				Window.getImGuiLayer().getPropertiesWindow().clearGameObjects();
				boxSelectStart.set(MouseListener.getScreen());
				boxSelect = true;
			}
			boxSelectEnd.set(MouseListener.getScreen());
			Vector2f start = MouseListener.screenToWorld(boxSelectStart);
			Vector2f end = MouseListener.screenToWorld(boxSelectEnd);
			Vector2f halfSize = new Vector2f(end).sub(start).div(2.0f);
			DebugDraw.addBox(new Vector2f(start).add(halfSize), new Vector2f(halfSize).mul(2.0f), 0);
		} else if (boxSelect) {
			boxSelect = false;
			int startX = (int) boxSelectStart.x, startY = (int) boxSelectStart.y;
			int endX = (int) boxSelectEnd.x, endY = (int) boxSelectEnd.y;
			boxSelectStart.zero();
			boxSelectEnd.zero();
			if (startX > endX) {
				int tmp = startX;
				startX = endX;
				endX = tmp;
			}
			if (startY > endY) {
				int tmp = startY;
				startY = endY;
				endY = tmp;
			}
			float[] gameObjectIds = picker.readPixels(new Vector2i(startX, startY), new Vector2i(endX, endY));
			Set<Integer> uniqueIds = new HashSet<>();
			for (float objId : gameObjectIds) {
				uniqueIds.add((int) objId);
			}
			for (Integer gameObjectId : uniqueIds) {
				GameObject pickedObj = Window.getScene().getGameObject(gameObjectId);
				if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null) {
					Window.getImGuiLayer().getPropertiesWindow().addActiveGameObject(pickedObj);
				}
			}
		} else if (!ImGuiLayer.getWantCaptureMouse() && boxSelect) {
			boxSelect = false;
			boxSelectStart.zero();
			boxSelectEnd.zero();
			Window.getImGuiLayer().getPropertiesWindow().clearGameObjects();
		}
	}

	private boolean blockInSquare(float x, float y) {
		PropertiesWindow propertiesWindow = Window.getImGuiLayer().getPropertiesWindow();
		Vector2f start = new Vector2f(x, y);
		Vector2f end = new Vector2f(start).add(new Vector2f(CONSTANTS.GRID_WIDTH.getIntValue(), CONSTANTS.GRID_HEIGHT.getIntValue()));
		Vector2f startScreenf = MouseListener.worldToScreen(start);
		Vector2f endScreenf = MouseListener.worldToScreen(end);
		Vector2i startScreen = new Vector2i((int) startScreenf.x + 2, (int) startScreenf.y + 2);
		Vector2i endScreen = new Vector2i((int) endScreenf.x - 2, (int) endScreenf.y - 2);
		float[] gameObjectIds = propertiesWindow.getPicker().readPixels(startScreen, endScreen);
		for (float gameObjectId : gameObjectIds) {
			if (gameObjectId >= 0) {
				GameObject pickedObj = Window.getScene().getGameObject((int) gameObjectId);
				if (pickedObj.getComponent(NonPickable.class) == null) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void start() {
	}
}
