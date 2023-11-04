package engine.ruby;

import engine.components.GameObject;
import engine.observers.EventSystem;
import engine.observers.Observer;
import engine.observers.events.Event;
import engine.physics2d.Physics2D;
import engine.renderer.*;
import engine.scenes.LevelEditorScene;
import engine.scenes.LevelScene;
import engine.scenes.Scene;
import engine.scenes.Scenes;
import engine.util.AssetPool;
import engine.util.CONSTANTS;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.opengl.GL;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.logging.Logger;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {
	private static final String iconPath = CONSTANTS.LOGO_PATH.getValue();
	private static final String title = "Mario";
	public static int maxWidth = 0, maxHeight = 0;
	private static int width = 640, height = 480;
	private static Window instance = null;
	private static Scene currentScene = null;
	private static ImGuiLayer imGuiLayer = null;
	private static FontRenderer fontRenderer = null;
	private long glfwWindow, audioDevice, audioContext;
	private FrameBuffer frameBuffer;
	private Picker picker;
	private GameObject currentGameObject = null;
	private boolean runtimePlaying = false;

	private Window() {
		EventSystem.getInstance().addObserver(this);
	}


	public static Window getInstance() {
		if (instance == null) {
			instance = new Window();
		}
		return instance;
	}

	public static Scene getScene() {
		return currentScene;
	}

	public static LevelScene getLevelScene() {
		return (LevelScene) currentScene;
	}

	public static void changeScene(Scenes type) {
		if (currentScene != null) {
			currentScene.destroy();
		}
		getImGuiLayer().getPropertiesWindow().setActiveGameObject(null);
		switch (type) {
			case LEVEL_EDITOR -> {
				getInstance().runtimePlaying = false;
				getImGuiLayer().getGameViewWindow().setPlaying(false);
				currentScene = new LevelEditorScene();
			}
			case LEVEL_SCENE -> currentScene = new LevelScene();
			default -> {
				assert false : "Unknown scene '" + type + "'";
			}
		}
		currentScene.start();
	}

	public static FrameBuffer getFrameBuffer() {
		return getInstance().frameBuffer;
	}

	public static int getMaxWidth() {
		return maxWidth;
	}

	public static int getMaxHeight() {
		return maxHeight;
	}

	public static int getWidth() {
		return glfwGetWindowAttrib(getInstance().glfwWindow, GLFW_MAXIMIZED) == GLFW_TRUE
				? maxWidth
				: width;
	}

	public static void setWidth(int w) {
		width = w;
	}

	public static int getHeight() {
		return glfwGetWindowAttrib(getInstance().glfwWindow, GLFW_MAXIMIZED) == GLFW_TRUE
				? maxHeight
				: height;
	}

	public static void setHeight(int h) {
		height = h;
	}

	public static float getAspectRatio() {
		return (float) getMaxWidth() / (float) getMaxHeight();
	}

	public static ImGuiLayer getImGuiLayer() {
		return imGuiLayer;
	}

	public static Physics2D getPhysics2D() {
		return currentScene.getPhysics2D();
	}

	private void init() {
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
		glfwWindow = glfwCreateWindow(width, height, title, NULL, NULL);
		if (glfwWindow == NULL) {
			throw new IllegalStateException("Failed to create GLFW window");
		}

		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		IntBuffer channels = BufferUtils.createIntBuffer(1);
		ByteBuffer image = stbi_load(iconPath, width, height, channels, 0);
		if (image != null) {
			GLFWImage icon = GLFWImage.malloc();
			icon.set(width.get(0), height.get(0), image);
			GLFWImage.Buffer icons = GLFWImage.malloc(1);
			icons.put(0, icon);
			glfwSetWindowIcon(glfwWindow, icons);
			icons.free();
			stbi_image_free(image);
		} else {
			assert false : "Error: (Window) Could not load image '" + iconPath + "'";
		}

		glfwSetErrorCallback(
				(error, description) -> System.err.printf("Error %d: %s%n", error, description));
		glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePoseCallback);
		glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
		glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
		glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
		glfwMakeContextCurrent(glfwWindow);
		glfwSwapInterval(1);
		glfwShowWindow(glfwWindow);

		String defaultDeviceName = alcGetString(NULL, ALC_DEFAULT_DEVICE_SPECIFIER);
		audioDevice = ALC10.alcOpenDevice(defaultDeviceName);
		assert audioDevice != NULL : "Failed to open audio device.";
		audioContext = ALC10.alcCreateContext(audioDevice, (IntBuffer) null);
		assert audioContext != NULL : "Failed to create OpenAL context.";
		ALC10.alcMakeContextCurrent(audioContext);
		ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
		assert alcCapabilities.OpenALC10 : "OpenALC10 is not supported.";
		assert alcCapabilities.OpenALC11 : "OpenALC11 is not supported.";
		AL.createCapabilities(alcCapabilities);

		glfwGetWindowSize(glfwWindow, width, height);
		maxHeight = height.get(0);
		maxWidth = width.get(0);

		GL.createCapabilities();
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		frameBuffer = new FrameBuffer(getMaxWidth(), getMaxHeight());
		picker = new Picker(getMaxWidth(), getMaxHeight());
		glViewport(0, 0, getMaxWidth(), getMaxHeight());
		imGuiLayer = new ImGuiLayer(glfwWindow, picker);
		imGuiLayer.initImGui();
		changeScene(Scenes.LEVEL_EDITOR);
		fontRenderer = new FontRenderer();
	}

	public void loop() {
		double startTime = glfwGetTime();
		double endTime, dt = -1.0f;
		Shader shader = AssetPool.getShader(CONSTANTS.DEFAULT_SHADER_PATH.getValue());
		Shader pickerShader = AssetPool.getShader(CONSTANTS.PICKER_SHADER_PATH.getValue());
		while (!glfwWindowShouldClose(glfwWindow)) {
			glfwPollEvents();

			glDisable(GL_BLEND);
			picker.bind();
			glViewport(0, 0, getMaxWidth(), getMaxHeight());
			glClearColor(0, 0, 0, 0);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			Renderer.bindShader(pickerShader);
			currentScene.render();
			picker.unbind();
			glEnable(GL_BLEND);

			DebugDraw.beginFrame();
			frameBuffer.bind();
			Vector4f color = currentScene.getCamera().clearColor;
			glClearColor(color.x, color.y, color.z, color.w);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			if (dt >= 0) {
				Renderer.bindShader(shader);
				if (runtimePlaying) {
					currentScene.update((float) dt);
				} else {
					currentScene.editorUpdate((float) dt);
				}
				currentScene.render();
				if (runtimePlaying) {
					String coins = String.format("Coins %03d", getLevelScene().coins);
					float x = getScene().getCamera().getPosition().x + 5.0f;
					float y = getScene().getCamera().getPosition().y + 2.75f;
					fontRenderer.write(coins, x, y, 0.0025f, new Vector3f(1));
				}
				DebugDraw.draw();
			}
			frameBuffer.unbind();

			imGuiLayer.update((float) dt, currentScene);
			MouseListener.endFrame();
			KeyListener.endFrame();
			glfwSwapBuffers(glfwWindow);


			endTime = glfwGetTime();
			dt = endTime - startTime;
			startTime = endTime;
		}
	}

	public void run() {
		System.out.printf("Window size: %dx%d%n", width, height);
		System.out.printf("Window title: %s%n", title);
		init();
		loop();
		alcDestroyContext(audioContext);
		alcCloseDevice(audioDevice);
		glfwFreeCallbacks(glfwWindow);
		glfwDestroyWindow(glfwWindow);
		glfwTerminate();
		try {
			Objects.requireNonNull(glfwSetErrorCallback(null)).free();
		} catch (NullPointerException e) {
			Logger logger = Logger.getLogger(title);
			logger.severe(e.getMessage());
			System.out.println("Error callback already freed");
		}
	}

	@Override
	public void onNotify(GameObject gameObject, Event event) {
		switch (event.getEventType()) {
			case GAME_ENGINE_START -> {
				runtimePlaying = true;
				currentScene.export();
				currentGameObject = getImGuiLayer().getPropertiesWindow().getActiveGameObject();
				changeScene(Scenes.LEVEL_SCENE);
			}
			case GAME_ENGINE_STOP -> {
				runtimePlaying = false;
				changeScene(Scenes.LEVEL_EDITOR);
				getImGuiLayer().getPropertiesWindow().setActiveGameObject(currentGameObject);
			}
			case LOAD_LEVEL -> changeScene(Scenes.LEVEL_EDITOR);
			case SAVE_LEVEL -> currentScene.export();
			case USER_EVENT -> System.out.println("User event");
			default -> {
				assert false : "Unknown event '" + event.getEventType() + "'";
			}
		}
	}
}
