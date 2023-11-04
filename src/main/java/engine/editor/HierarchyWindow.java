package engine.editor;

import engine.components.GameObject;
import engine.ruby.Window;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.List;

public class HierarchyWindow {
	public static void imGui() {
		ImGui.begin("Scene Heirarchy");
		List<GameObject> gameObjects = Window.getScene().getGameObjects();
		int idx = 0;
		for (GameObject go : gameObjects) {
			if (go.isSerializable()) {
				ImGui.pushID(idx++);
				boolean node = ImGui.treeNode(ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.FramePadding, go.getName());
				ImGui.popID();
				if (ImGui.beginDragDropSource()) {
					ImGui.setDragDropPayload("SceneHierarchy", go);
					ImGui.text(go.getName());
					ImGui.endDragDropSource();
				}
				if (ImGui.beginDragDropTarget()) {
					Object payload = ImGui.acceptDragDropPayload("SceneHierarchy");
					if (payload != null && payload.getClass().isAssignableFrom(GameObject.class)) {
						GameObject gameObject = (GameObject) payload;
						System.out.println("Recieved: " + gameObject.getName());
					}
					ImGui.endDragDropTarget();
				}
				if (node) {
					ImGui.treePop();
				}
			}
		}
		ImGui.end();
	}
}
