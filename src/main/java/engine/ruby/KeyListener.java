package engine.ruby;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {
	private static KeyListener instance = null;
	private final boolean[] keyPressed = new boolean[350];
	private final boolean[] keyBeginPress = new boolean[350];

	private KeyListener() {
	}

	public static void endFrame() {
		Arrays.fill(getInstance().keyBeginPress, false);
	}

	public static KeyListener getInstance() {
		if (KeyListener.instance == null) {
			KeyListener.instance = new KeyListener();
		}
		return instance;
	}

	public static void keyCallback(
			long ignoredWindow, int key, int ignoredScancode, int action, int ignoredMods) {
		if (key < getInstance().keyPressed.length && key >= 0) {
			getInstance().keyPressed[key] = action != GLFW_RELEASE;
			getInstance().keyBeginPress[key] = action != GLFW_RELEASE;
		}
	}

	public static boolean isKeyPressed(int keyCode) {
		return keyCode < getInstance().keyPressed.length && getInstance().keyPressed[keyCode];
	}

	public static boolean keyBeginPress(int keyCode) {
		return keyCode < getInstance().keyBeginPress.length && getInstance().keyBeginPress[keyCode];
	}
}
