package engine.ruby;

import engine.renderer.Camera;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
	private static MouseListener instance = null;
	private final boolean[] mouseButtonPressed = new boolean[9];
	private final Vector2f viewportPos = new Vector2f();
	private final Vector2f viewportSize = new Vector2f();
	private double scrollX, scrollY;
	private double xPos, yPos;
	private float lastWorldX, lastWorldY, worldX, worldY;
	private boolean isDragging = false;
	private int mouseButtonDown = 0;

	private MouseListener() {
		scrollX = scrollY = 0;
		xPos = yPos = 0;
	}

	public static MouseListener getInstance() {
		if (MouseListener.instance == null) {
			MouseListener.instance = new MouseListener();
		}
		return instance;
	}

	public static void mousePoseCallback(long ignoredWindow, double xPos, double yPos) {
		if (!ImGuiLayer.getWantCaptureMouse()) {
			clear();
		}
		getInstance().isDragging = getInstance().mouseButtonDown > 0;
		getInstance().lastWorldX = getInstance().worldX;
		getInstance().lastWorldY = getInstance().worldY;
		getInstance().xPos = xPos;
		getInstance().yPos = yPos;
		getWorld();
	}

	public static void mouseButtonCallback(
			long ignoredWindow, int button, int action, int ignoredMods) {
		if (action == GLFW_RELEASE) {
			getInstance().mouseButtonDown--;
			getInstance().isDragging = false;
		} else if (action == GLFW_PRESS) {
			getInstance().mouseButtonDown++;
		}
		if (button < getInstance().mouseButtonPressed.length) {
			getInstance().mouseButtonPressed[button] = action != GLFW_RELEASE;
		}
	}

	public static void mouseScrollCallback(long ignoredWindow, double xOffset, double yOffset) {
		getInstance().scrollX = xOffset;
		getInstance().scrollY = yOffset;
	}

	public static void endFrame() {
		getInstance().scrollX = getInstance().scrollY = 0;
		getInstance().lastWorldX = getInstance().worldX;
		getInstance().lastWorldY = getInstance().worldY;
	}

	public static void clear() {
		getInstance().scrollX = getInstance().scrollY = 0;
		getInstance().lastWorldX = getInstance().lastWorldY = getInstance().worldX = getInstance().worldY = 0;
		getInstance().xPos = getInstance().yPos = 0;
		getInstance().mouseButtonDown = 0;
		getInstance().isDragging = false;
		Arrays.fill(getInstance().mouseButtonPressed, false);
	}

	public static void setViewportPos(Vector2f pos) {
		getInstance().viewportPos.set(pos);
	}

	public static void setViewportSize(Vector2f size) {
		getInstance().viewportSize.set(size);
	}

	public static float getX() {
		return (float) getInstance().xPos;
	}

	public static float getY() {
		return (float) getInstance().yPos;
	}

	public static float getWorldDX() {
		return getInstance().lastWorldX - getInstance().worldX;
	}

	public static float getWorldDY() {
		return getInstance().lastWorldY - getInstance().worldY;
	}

	public static float getScrollX() {
		return (float) getInstance().scrollX;
	}

	public static float getScrollY() {
		return (float) getInstance().scrollY;
	}

	public static boolean isDragging() {
		return getInstance().isDragging;
	}

	public static float getScreenX() {
		return getScreen().x;
	}

	public static float getScreenY() {
		return getScreen().y;
	}

	public static Vector2f getScreen() {
		float currX = ((getX() - getInstance().viewportPos.x) / getInstance().viewportSize.x) * Window.getMaxWidth();
		float currY = (1 - ((getY() - getInstance().viewportPos.y) / getInstance().viewportSize.y)) * Window.getMaxHeight();
		return new Vector2f(currX, currY);
	}

	public static float getWorldX() {
		return getWorld().x;
	}

	public static float getWorldY() {
		return getWorld().y;
	}

	public static Vector2f getWorld() {
		float currX = ((getX() - getInstance().viewportPos.x) / getInstance().viewportSize.x) * 2.0f - 1.0f;
		float currY = (2.0f * (1.0f - ((getY() - getInstance().viewportPos.y) / getInstance().viewportSize.y))) - 1.0f;
		Vector4f tmp = new Vector4f(currX, currY, 0, 1);
		Camera camera = Window.getScene().getCamera();
		Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjectionMatrix());
		Matrix4f inverseView = new Matrix4f(camera.getInverseViewMatrix());
		tmp.mul(inverseView.mul(inverseProjection));
		getInstance().worldX = tmp.x;
		getInstance().worldY = tmp.y;
		return new Vector2f(tmp.x, tmp.y);
	}

	public static Vector2f screenToWorld(Vector2f screen) {
		Vector2f normalized = new Vector2f(
				screen.x / Window.getWidth(),
				screen.y / Window.getHeight()
		);
		normalized.mul(2.0f).sub(new Vector2f(1.0f, 1.0f));
		Camera camera = Window.getScene().getCamera();
		Vector4f tmp = new Vector4f(normalized.x, normalized.y, 0, 1);
		Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjectionMatrix());
		Matrix4f inverseView = new Matrix4f(camera.getInverseViewMatrix());
		tmp.mul(inverseView.mul(inverseProjection));
		return new Vector2f(tmp.x, tmp.y);
	}

	public static Vector2f worldToScreen(Vector2f world) {
		Camera camera = Window.getScene().getCamera();
		Vector4f tmp = new Vector4f(world.x, world.y, 0, 1);
		Matrix4f projection = new Matrix4f(camera.getProjectionMatrix());
		Matrix4f view = new Matrix4f(camera.getViewMatrix());
		tmp.mul(projection.mul(view));
		Vector2f normalized = new Vector2f(tmp.x, tmp.y).mul(1.0f / tmp.w);
		normalized.add(new Vector2f(1.0f, 1.0f)).mul(0.5f);
		normalized.mul(new Vector2f(Window.getWidth(), Window.getHeight()));
		return normalized;
	}

	public static boolean mouseButtonDown(int button) {
		if (button < getInstance().mouseButtonPressed.length) {
			return getInstance().mouseButtonPressed[button];
		}
		return false;
	}
}
