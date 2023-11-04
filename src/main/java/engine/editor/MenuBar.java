package engine.editor;

import engine.observers.EventSystem;
import engine.observers.events.Event;
import engine.observers.events.EventType;
import engine.ruby.Window;
import engine.util.CONSTANTS;
import imgui.ImGui;

import java.io.File;
import java.util.Arrays;

public class MenuBar {
	public static void imGui() {
		ImGui.beginMenuBar();

		if (ImGui.beginMenu("File")) {
			if (ImGui.menuItem("Save", "Ctrl+S")) {
				EventSystem.getInstance().notify(null, new Event(EventType.SAVE_LEVEL));
			}

			if (ImGui.beginMenu("Load")) {
				String storePath = "%s/scenes/".formatted(CONSTANTS.RESOURCE_PATH.getValue());
				String[] files = new File(storePath).list();
				assert files != null;
				files = Arrays.stream(files).filter(f -> f.endsWith(".json")).toArray(String[]::new);
				for (String file : files) {
					if (ImGui.menuItem(file)) {
						Window.getScene().setDefaultScene(file.replace(".json", ""));
						EventSystem.getInstance().notify(null, new Event(EventType.LOAD_LEVEL));
					}
				}
				ImGui.endMenu();
			}

			ImGui.endMenu();
		}

		ImGui.endMenuBar();
	}
}
