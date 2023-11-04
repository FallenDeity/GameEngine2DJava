package engine.editor;

import engine.ruby.MouseListener;
import engine.ruby.Window;
import engine.scenes.Scene;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;

public class ToolBarViewWindow {
	private final static int RATE = 20;
	private final static int widgetHeight = 30;
	private static int fps = 0;
	private static float ms = 0;

	public static void imGui(float dt, Scene currentScene) {
		if (ImGui.getFrameCount() % RATE == 0 || fps == 0) {
			fps = (int) (1 / dt);
			ms = dt * 1000;
		}

		ImGui.setNextWindowBgAlpha(1.0f);
		ImGui.setNextWindowViewport(ImGui.getMainViewport().getID());
		ImGui.begin("Toolbar Window", ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoMove);
		ImGui.setWindowPos(-2, Window.getHeight() + 1, ImGuiCond.Always);
		ImGui.setWindowSize(Window.getWidth() + 4, widgetHeight, ImGuiCond.Always);

		String fpsString = "FPS: " + fps;
		ImGui.setCursorPosY(ImGui.getWindowHeight() / 2 - ImGui.calcTextSize(fpsString).y / 2);
		ImGui.setCursorPosX(ImGui.getWindowWidth() - ImGui.calcTextSize(fpsString).x - 10);
		ImGui.text("FPS: " + fps);

		String msString = "Frame Time: %.2fms".formatted(ms);
		ImGui.setCursorPosY(ImGui.getWindowHeight() / 2 - ImGui.calcTextSize(msString).y / 2);
		ImGui.setCursorPosX(ImGui.getWindowWidth() - ImGui.calcTextSize(msString + fpsString).x - 50);
		ImGui.text(msString);

		String mouseCoords = "Mouse: (%.2f, %.2f)".formatted(MouseListener.getWorldX(), MouseListener.getWorldY());
		ImGui.setCursorPosY(ImGui.getWindowHeight() / 2 - ImGui.calcTextSize(mouseCoords).y / 2);
		ImGui.setCursorPosX(ImGui.getWindowWidth() - ImGui.calcTextSize(mouseCoords + msString + fpsString).x - 100);
		ImGui.text(mouseCoords);

		String sceneName = (Window.getScene() == null ? "default" : Window.getScene().getDefaultScene()) + ".json";
		ImGui.setCursorPosY(ImGui.getWindowHeight() / 2 - ImGui.calcTextSize(sceneName).y / 2);
		ImGui.setCursorPosX(10);
		ImGui.text(sceneName);
		ImGui.end();
	}

	public static int getWidgetHeight() {
		return widgetHeight;
	}
}
