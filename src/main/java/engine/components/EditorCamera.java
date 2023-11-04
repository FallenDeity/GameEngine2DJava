package engine.components;

import engine.renderer.Camera;
import engine.renderer.DebugDraw;
import engine.ruby.ImGuiLayer;
import engine.ruby.KeyListener;
import engine.ruby.MouseListener;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.GLFW.*;

public class EditorCamera extends Component {
	private final int refreshRate;
	private final Camera editorCamera;
	private float dragDebounce;
	private Vector2f originalPos;
	private boolean resetButton = false;
	private float lerpTime = 0.0f;


	public EditorCamera(Camera editorCamera) {
		this.editorCamera = editorCamera;
		originalPos = new Vector2f();
		GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		refreshRate = vidMode != null ? vidMode.refreshRate() : 60;
		dragDebounce = 2f / refreshRate;
	}

	@Override
	public void update(float dt) {

	}

	@Override
	public void editorUpdate(float dt) {
		if (ImGuiLayer.getWantCaptureMouse() && !resetButton) {
			if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT) && dragDebounce > 0.0f) {
				originalPos = MouseListener.getWorld();
				dragDebounce -= dt;
				return;
			} else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
				Vector2f mousePos = MouseListener.getWorld();
				Vector2f delta = new Vector2f(mousePos).sub(originalPos);
				float sensitivity = 30.0f;
				editorCamera.setPosition(editorCamera.getPosition().sub(delta.mul(dt).mul(sensitivity)));
				originalPos.lerp(mousePos, dt);
			}
			if (dragDebounce <= 0.0 && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
				dragDebounce = 2f / refreshRate;
			}
			if (MouseListener.getScrollY() != 0.0f) {
				float zoomSensitivity = 0.3f;
				float zoomAmount = (float) Math.pow(Math.abs(MouseListener.getScrollY() * zoomSensitivity), 1 / editorCamera.getZoom());
				zoomAmount *= -Math.signum(MouseListener.getScrollY());
				editorCamera.addZoom(zoomAmount);
			}
			if (KeyListener.isKeyPressed(GLFW_KEY_PERIOD)) {
				resetButton = true;
			}
		} else {
			dragDebounce = 2f / refreshRate;
		}
		if (resetButton) {
			editorCamera.getPosition().lerp(new Vector2f(), lerpTime);
			editorCamera.setZoom(editorCamera.getZoom() + ((1.0f - editorCamera.getZoom()) * lerpTime));
			lerpTime += dt * 0.1f;
			if (Math.abs(editorCamera.getPosition().x) <= 0.5f && Math.abs(editorCamera.getPosition().y) <= 0.5f && Math.abs(editorCamera.getZoom() - 1.0f) <= 0.01f) {
				lerpTime = 0.0f;
				editorCamera.getPosition().set(0.0f, 0.0f);
				editorCamera.setZoom(1.0f);
				resetButton = false;
			}
		}
		DebugDraw.addLine(new Vector2f(0.0f, -4.5f), new Vector2f(0.0f, 3.0f));
		DebugDraw.addLine(new Vector2f(-100.0f, 3.0f), new Vector2f(100.0f, 3.0f), new Vector3f(0.0f, 0.0f, 1.0f));
		DebugDraw.addLine(new Vector2f(-100.0f, -1.5f), new Vector2f(100.0f, -1.5f), new Vector3f(0.0f, 0.0f, 1.0f));
		DebugDraw.addLine(new Vector2f(-100.0f, -4.5f), new Vector2f(100.0f, -4.5f), new Vector3f(0.0f, 0.0f, 1.0f));
	}

	@Override
	public void start() {

	}
}
