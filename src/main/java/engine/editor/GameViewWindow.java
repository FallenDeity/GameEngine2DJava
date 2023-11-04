package engine.editor;

import engine.observers.EventSystem;
import engine.observers.events.Event;
import engine.observers.events.EventType;
import engine.ruby.MouseListener;
import engine.ruby.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;

public class GameViewWindow {
	private boolean isPlaying = false;
	private float leftX, rightX, topY, bottomY;

	public void imGui() {
		ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.MenuBar);
		ImGui.beginMenuBar();
		if (ImGui.menuItem("Play", "Ctrl+P", isPlaying, !isPlaying)) {
			isPlaying = !isPlaying;
			EventSystem.getInstance().notify(null, new Event(EventType.GAME_ENGINE_START));
		}
		if (ImGui.menuItem("Stop", "Ctrl+S", !isPlaying, isPlaying)) {
			isPlaying = !isPlaying;
			EventSystem.getInstance().notify(null, new Event(EventType.GAME_ENGINE_STOP));
		}
		ImGui.endMenuBar();
		ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());
		ImVec2 windowSize = getWindowSize(), windowPos = getWindowPos(windowSize);
		ImGui.setCursorPos(windowPos.x, windowPos.y);
		ImVec2 topLeft = ImGui.getCursorScreenPos();
		leftX = topLeft.x;
		rightX = topLeft.x + windowSize.x;
		bottomY = topLeft.y - ToolBarViewWindow.getWidgetHeight();
		topY = topLeft.y + windowSize.y;
		int texID = Window.getFrameBuffer().getTexture().getID();
		ImGui.image(texID, windowSize.x, windowSize.y, 0, 1, 1, 0);
		MouseListener.setViewportPos(new Vector2f(topLeft.x, topLeft.y - ToolBarViewWindow.getWidgetHeight()));
		MouseListener.setViewportSize(new Vector2f(windowSize.x, windowSize.y));
		ImGui.end();
	}

	private ImVec2 getWindowSize() {
		ImVec2 size = ImGui.getContentRegionAvail();
		float aspW = size.x, aspH = aspW / Window.getAspectRatio();
		if (aspH > size.y) {
			aspH = size.y;
			aspW = aspH * Window.getAspectRatio();
		}
		return new ImVec2(aspW, aspH);
	}

	private ImVec2 getWindowPos(ImVec2 size) {
		ImVec2 pos = ImGui.getContentRegionAvail();
		float viewX = (pos.x - size.x) / 2.0f, viewY = (pos.y - size.y) / 2.0f;
		return new ImVec2(viewX + ImGui.getCursorPosX(), viewY + ImGui.getCursorPosY());
	}

	public boolean getWantCaptureMouse() {
		return MouseListener.getX() >= leftX && MouseListener.getX() <= rightX &&
				MouseListener.getY() >= bottomY && MouseListener.getY() <= topY;
	}

	public void setPlaying(boolean playing) {
		isPlaying = playing;
	}
}
