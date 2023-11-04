package engine.ruby;

import engine.editor.*;
import engine.renderer.Picker;
import engine.scenes.Scene;
import engine.util.CONSTANTS;
import imgui.*;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30C.glBindFramebuffer;

public class ImGuiLayer {
	private final static GameViewWindow gameViewWindow = new GameViewWindow();
	private final long windowId;
	private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
	private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
	private final PropertiesWindow propertiesWindow;

	public ImGuiLayer(long windowId, Picker picker) {
		this.windowId = windowId;
		propertiesWindow = new PropertiesWindow(picker);
	}

	public static boolean getWantCaptureMouse() {
		return gameViewWindow.getWantCaptureMouse();
	}

	public PropertiesWindow getPropertiesWindow() {
		return propertiesWindow;
	}

	public GameViewWindow getGameViewWindow() {
		return gameViewWindow;
	}

	public void update(float dt, Scene currentScene) {
		startFrame(dt);
		setupDockspace();
		currentScene.imGui();
		gameViewWindow.imGui();
		propertiesWindow.imGui();
		HierarchyWindow.imGui();
		ToolBarViewWindow.imGui(dt, currentScene);
		endFrame();
	}

	public void setupDockspace() {
		int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;
		ImGuiViewport viewport = ImGui.getMainViewport();
		ImGui.setNextWindowPos(viewport.getWorkPosX(), viewport.getWorkPosY());
		ImGui.setNextWindowSize(viewport.getWorkSizeX(), viewport.getWorkSizeY());
		ImGui.setNextWindowViewport(viewport.getID());
		ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.Always);
		ImGui.setNextWindowSize(Window.getWidth(), Window.getHeight());
		ImGui.pushStyleColor(ImGuiCol.WindowBg, 0.0f, 0.0f, 0.0f, 0.0f);
		ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
		ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
		ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
		ImGui.begin("Dockspace Window", new ImBoolean(true), windowFlags);
		ImGui.popStyleColor();
		ImGui.popStyleVar(3);
		ImGui.dockSpace(ImGui.getID("Dockspace"));
		MenuBar.imGui();
		ImGui.end();
	}

	public void initImGui() {
		ImGui.createContext();
		final ImGuiIO io = ImGui.getIO();

		try {
			Files.createDirectories(Path.of(CONSTANTS.IMGUI_PATH.getValue()));
		} catch (IOException e) {
			assert false : "Error creating imgui save directory";
		}
		io.setIniFilename(CONSTANTS.IMGUI_PATH.getValue() + "imgui.ini");
		io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard | ImGuiConfigFlags.DockingEnable);
		io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
		io.addBackendFlags(ImGuiBackendFlags.HasMouseCursors | ImGuiBackendFlags.HasSetMousePos);
		io.setBackendPlatformName("imgui_java_impl_glfw");

		final int[] keyMap = new int[ImGuiKey.COUNT];
		keyMap[ImGuiKey.Tab] = GLFW_KEY_TAB;
		keyMap[ImGuiKey.LeftArrow] = GLFW_KEY_LEFT;
		keyMap[ImGuiKey.RightArrow] = GLFW_KEY_RIGHT;
		keyMap[ImGuiKey.UpArrow] = GLFW_KEY_UP;
		keyMap[ImGuiKey.DownArrow] = GLFW_KEY_DOWN;
		keyMap[ImGuiKey.PageUp] = GLFW_KEY_PAGE_UP;
		keyMap[ImGuiKey.PageDown] = GLFW_KEY_PAGE_DOWN;
		keyMap[ImGuiKey.Home] = GLFW_KEY_HOME;
		keyMap[ImGuiKey.End] = GLFW_KEY_END;
		keyMap[ImGuiKey.Insert] = GLFW_KEY_INSERT;
		keyMap[ImGuiKey.Delete] = GLFW_KEY_DELETE;
		keyMap[ImGuiKey.Backspace] = GLFW_KEY_BACKSPACE;
		keyMap[ImGuiKey.Space] = GLFW_KEY_SPACE;
		keyMap[ImGuiKey.Enter] = GLFW_KEY_ENTER;
		keyMap[ImGuiKey.Escape] = GLFW_KEY_ESCAPE;
		keyMap[ImGuiKey.KeyPadEnter] = GLFW_KEY_KP_ENTER;
		keyMap[ImGuiKey.A] = GLFW_KEY_A;
		keyMap[ImGuiKey.C] = GLFW_KEY_C;
		keyMap[ImGuiKey.V] = GLFW_KEY_V;
		keyMap[ImGuiKey.X] = GLFW_KEY_X;
		keyMap[ImGuiKey.Y] = GLFW_KEY_Y;
		keyMap[ImGuiKey.Z] = GLFW_KEY_Z;

		io.setKeyMap(keyMap);

		io.setGetClipboardTextFn(
				new ImStrSupplier() {
					@Override
					public String get() {
						final String clipboardString = glfwGetClipboardString(windowId);
						return clipboardString != null ? clipboardString : "";
					}
				});

		io.setSetClipboardTextFn(
				new ImStrConsumer() {
					@Override
					public void accept(final String str) {
						glfwSetClipboardString(windowId, str);
					}
				});

		final GLFWErrorCallback prevErrorCallback = glfwSetErrorCallback(null);

		glfwSetErrorCallback(prevErrorCallback);
		glfwSetKeyCallback(windowId, this::keyCallback);
		glfwSetCharCallback(windowId, this::charCallback);
		glfwSetMouseButtonCallback(windowId, this::mouseButtonCallback);
		glfwSetScrollCallback(windowId, this::scrollCallback);
		glfwSetWindowSizeCallback(windowId, this::resizeCallback);

		final ImFontAtlas fontAtlas = io.getFonts();
		final ImFontConfig fontConfig = new ImFontConfig();
		fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());
		fontConfig.setPixelSnapH(true);
		fontAtlas.addFontFromFileTTF(CONSTANTS.FONT_PATH.getValue(), 22, fontConfig);
		fontConfig.destroy();
		imGuiGlfw.init(windowId, false);
		imGuiGl3.init("#version 330 core");
	}

	private void startFrame(float ignoredDt) {
		imGuiGlfw.newFrame();
		ImGui.newFrame();
	}

	private void endFrame() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, Window.getMaxWidth(), Window.getMaxHeight());
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		ImGui.render();
		imGuiGl3.renderDrawData(ImGui.getDrawData());
		long windowPtr = glfwGetCurrentContext();
		if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
			ImGui.updatePlatformWindows();
			ImGui.renderPlatformWindowsDefault();
			glfwMakeContextCurrent(windowPtr);
		}
	}

	public void destroyImGui() {
		imGuiGl3.dispose();
		imGuiGlfw.dispose();
		ImGui.destroyContext();
	}

	private void resizeCallback(long ignoredWindow, int width, int height) {
		Window.setWidth(width);
		Window.setHeight(height);
	}

	private void scrollCallback(long window, double xOffset, double yOffset) {
		ImGuiIO io = ImGui.getIO();
		io.setMouseWheelH((float) xOffset + io.getMouseWheelH());
		io.setMouseWheel((float) yOffset + io.getMouseWheel());
		MouseListener.mouseScrollCallback(window, xOffset, yOffset);
	}

	private void mouseButtonCallback(long window, int button, int action, int mods) {
		final boolean[] mouseDown = new boolean[5];
		mouseDown[0] = button == GLFW_MOUSE_BUTTON_1 && action != GLFW_RELEASE;
		mouseDown[1] = button == GLFW_MOUSE_BUTTON_2 && action != GLFW_RELEASE;
		mouseDown[2] = button == GLFW_MOUSE_BUTTON_3 && action != GLFW_RELEASE;
		mouseDown[3] = button == GLFW_MOUSE_BUTTON_4 && action != GLFW_RELEASE;
		mouseDown[4] = button == GLFW_MOUSE_BUTTON_5 && action != GLFW_RELEASE;
		ImGuiIO io = ImGui.getIO();
		io.setMouseDown(mouseDown);
		if (!io.getWantCaptureMouse() && mouseDown[1]) {
			ImGui.setWindowFocus(null);
		}
		MouseListener.mouseButtonCallback(window, button, action, mods);
	}

	private void charCallback(long ignoredWindow, int codepoint) {
		ImGuiIO io = ImGui.getIO();
		if (codepoint != GLFW_KEY_DELETE) {
			io.addInputCharacter(codepoint);
		}
	}

	private void keyCallback(long window, int key, int scancode, int action, int mods) {
		final ImGuiIO io = ImGui.getIO();
		if (action == GLFW_PRESS) {
			io.setKeysDown(key, true);
		} else if (action == GLFW_RELEASE) {
			io.setKeysDown(key, false);
		}
		io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
		io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
		io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
		io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));
		KeyListener.keyCallback(window, key, scancode, action, mods);
	}
}
