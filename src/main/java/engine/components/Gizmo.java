package engine.components;

import engine.components.sprites.Sprite;
import engine.editor.PropertiesWindow;
import engine.ruby.KeyListener;
import engine.ruby.MouseListener;
import engine.scenes.Scene;
import engine.util.Prefabs;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_N;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Gizmo extends Component {
	private final float scale = 120.0f;
	private final float gizmoWidth = 16.0f / scale, gizmoHeight = 48.0f / scale;
	private final Vector4f xArrowColor = new Vector4f(1, 0, 0, 1), yArrowColor = new Vector4f(0, 1, 0, 1);
	private final Vector4f xAxisHoverColor = new Vector4f(0, 0, 1, 1), yAxisHoverColor = new Vector4f(0, 0, 1, 1);
	private final Vector2f xOffset = new Vector2f(24.0f / scale, -6.0f / scale), yOffset = new Vector2f(-7.0f / scale, 21.0f / scale);
	private final GameObject xArrow, yArrow;
	private final PropertiesWindow window;
	private final SpriteRenderer xArrowRenderer, yArrowRenderer;
	protected GameObject activeGameObject = null;
	protected boolean xActive = false, yActive = false, use = false, isSnapping = false;

	public Gizmo(Scene scene, Sprite sprite, PropertiesWindow window) {
		this.window = window;
		xArrow = Prefabs.generateSpriteObject(sprite, gizmoWidth, gizmoHeight);
		yArrow = Prefabs.generateSpriteObject(sprite, gizmoWidth, gizmoHeight);
		xArrowRenderer = xArrow.getComponent(SpriteRenderer.class);
		yArrowRenderer = yArrow.getComponent(SpriteRenderer.class);
		xArrow.transform.setZIndex(100);
		yArrow.transform.setZIndex(100);
		xArrow.addComponent(new NonPickable());
		yArrow.addComponent(new NonPickable());
		scene.addGameObjectToScene(xArrow);
		scene.addGameObjectToScene(yArrow);
	}

	private void setActiveGameObject(GameObject obj) {
		activeGameObject = obj;
		xArrowRenderer.setColor(xArrowColor);
		yArrowRenderer.setColor(yArrowColor);
	}

	private void clearActiveGameObject() {
		activeGameObject = null;
		xArrowRenderer.setColor(new Vector4f(0, 0, 0, 0));
		yArrowRenderer.setColor(new Vector4f(0, 0, 0, 0));
	}

	@Override
	public void update(float dt) {
		if (use) clearActiveGameObject();
		xArrow.getComponent(SpriteRenderer.class).setColor(new Vector4f(0, 0, 0, 0));
		yArrow.getComponent(SpriteRenderer.class).setColor(new Vector4f(0, 0, 0, 0));
	}

	@Override
	public void editorUpdate(float dt) {
		if (!use) return;
		activeGameObject = window.getActiveGameObject();
		if (activeGameObject != null) {
			setActiveGameObject(activeGameObject);
		} else {
			clearActiveGameObject();
			return;
		}
		boolean xHover = checkXHoverState(), yHover = checkYHoverState();
		if ((xHover || xActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
			xActive = true;
			yActive = false;
		} else if ((yHover || yActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
			yActive = true;
			xActive = false;
		} else {
			xActive = false;
			yActive = false;
		}
		if (activeGameObject != null) {
			xArrow.transform.getPosition().set(activeGameObject.transform.getPosition());
			yArrow.transform.getPosition().set(activeGameObject.transform.getPosition());
			xArrow.transform.getPosition().add(xOffset);
			yArrow.transform.getPosition().add(yOffset);
		}
		if (KeyListener.isKeyPressed(GLFW_KEY_N)) {
			isSnapping = !isSnapping;
		}
	}

	@Override
	public void start() {
		xArrow.transform.setRotation(90);
		yArrow.transform.setRotation(180);
		xArrow.setNotSerializable();
		yArrow.setNotSerializable();
	}

	private boolean checkXHoverState() {
		Vector2f mousePos = MouseListener.getWorld();
		if (mousePos.x >= xArrow.transform.getPosition().x - (gizmoHeight / 2.0f) &&
				mousePos.x <= xArrow.transform.getPosition().x + (gizmoHeight / 2.0f) &&
				mousePos.y >= xArrow.transform.getPosition().y - (gizmoWidth / 2.0f) &&
				mousePos.y <= xArrow.transform.getPosition().y + (gizmoWidth / 2.0f)) {
			xArrowRenderer.setColor(xAxisHoverColor);
			return true;
		}
		xArrowRenderer.setColor(xArrowColor);
		return false;
	}

	private boolean checkYHoverState() {
		Vector2f mousePos = MouseListener.getWorld();
		if (mousePos.x <= yArrow.transform.getPosition().x + (gizmoWidth / 2.0f) &&
				mousePos.x >= yArrow.transform.getPosition().x - (gizmoWidth / 2.0f) &&
				mousePos.y <= yArrow.transform.getPosition().y + (gizmoHeight / 2.0f) &&
				mousePos.y >= yArrow.transform.getPosition().y - (gizmoHeight / 2.0f)) {
			yArrowRenderer.setColor(yAxisHoverColor);
			return true;
		}
		yArrowRenderer.setColor(yArrowColor);
		return false;
	}

	public void setUse(boolean use) {
		this.use = use;
		if (!use) {
			clearActiveGameObject();
		}
	}
}
