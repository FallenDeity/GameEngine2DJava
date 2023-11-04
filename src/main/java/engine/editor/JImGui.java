package engine.editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class JImGui {
	public static void drawVec2Control(String name, Vector2f values) {
		drawVec2Control(name, values, 0.0f);
	}

	public static void drawVec2Control(String name, Vector2f values, float reset) {
		drawVec2Control(name, values, reset, 220.0f);
	}

	public static void drawVec2Control(String name, Vector2f values, float reset, float width) {
		ImGui.pushID(name);

		ImGui.columns(2);
		ImGui.setColumnWidth(0, width);
		ImGui.text(name);
		ImGui.nextColumn();

		ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0.0f, 4.0f);
		float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
		Vector2f buttonSize = new Vector2f(lineHeight + 3.0f, lineHeight);
		float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 2.0f) / 2.0f;

		ImGui.pushItemWidth(widthEach);
		ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1.0f);

		if (ImGui.button("X", buttonSize.x, buttonSize.y)) {
			values.x = reset;
		}
		ImGui.popStyleColor(3);

		ImGui.sameLine();
		ImFloat imFloat = new ImFloat(values.x);
		ImGui.inputFloat("##X", imFloat);
		ImGui.popItemWidth();
		ImGui.sameLine();

		ImGui.pushItemWidth(widthEach);
		ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);
		if (ImGui.button("Y", buttonSize.x, buttonSize.y)) {
			values.y = reset;
		}
		ImGui.popStyleColor(3);

		ImGui.sameLine();
		ImFloat imFloat2 = new ImFloat(values.y);
		ImGui.inputFloat("##Y", imFloat2);
		ImGui.popItemWidth();
		ImGui.sameLine();
		ImGui.nextColumn();

		values.x = imFloat.get();
		values.y = imFloat2.get();

		ImGui.popStyleVar();
		ImGui.columns(1);
		ImGui.dummy(0.0f, 1.0f);
		ImGui.popID();
	}

	public static void drawVec3Control(String name, Vector3f values) {
		drawVec3Control(name, values, 0.0f);
	}

	public static void drawVec3Control(String name, Vector3f values, float reset) {
		drawVec3Control(name, values, reset, 220.0f);
	}

	public static void drawVec3Control(String name, Vector3f values, float reset, float width) {
		ImGui.pushID(name);

		ImGui.columns(2);
		ImGui.setColumnWidth(0, width);
		ImGui.text(name);
		ImGui.nextColumn();

		ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0.0f, 4.0f);
		float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
		Vector2f buttonSize = new Vector2f(lineHeight + 3.0f, lineHeight);
		float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 3.0f) / 3.0f;

		ImGui.pushItemWidth(widthEach);
		ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1.0f);

		if (ImGui.button("X", buttonSize.x, buttonSize.y)) {
			values.x = reset;
		}
		ImGui.popStyleColor(3);

		ImGui.sameLine();
		ImFloat imFloat = new ImFloat(values.x);
		ImGui.inputFloat("##X", imFloat);
		ImGui.popItemWidth();
		ImGui.sameLine();

		ImGui.pushItemWidth(widthEach);
		ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);
		if (ImGui.button("Y", buttonSize.x, buttonSize.y)) {
			values.y = reset;
		}
		ImGui.popStyleColor(3);

		ImGui.sameLine();
		ImFloat imFloat2 = new ImFloat(values.y);
		ImGui.inputFloat("##y", imFloat2);
		ImGui.popItemWidth();
		ImGui.sameLine();

		ImGui.pushItemWidth(widthEach);
		ImGui.pushStyleColor(ImGuiCol.Button, 0.1f, 0.2f, 0.8f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.2f, 0.3f, 0.9f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.2f, 0.8f, 1.0f);
		if (ImGui.button("Z", buttonSize.x, buttonSize.y)) {
			values.z = reset;
		}
		ImGui.popStyleColor(3);

		ImGui.sameLine();
		ImFloat imFloat3 = new ImFloat(values.z);
		ImGui.inputFloat("##z", imFloat3);
		ImGui.popItemWidth();
		ImGui.sameLine();
		ImGui.nextColumn();

		values.x = imFloat.get();
		values.y = imFloat2.get();
		values.z = imFloat3.get();

		ImGui.popStyleVar();
		ImGui.columns(1);
		ImGui.dummy(0.0f, 1.0f);
		ImGui.popID();
	}

	public static void drawVec4Control(String name, Vector4f values) {
		drawVec4Control(name, values, 0.0f);
	}

	public static void drawVec4Control(String name, Vector4f values, float reset) {
		drawVec4Control(name, values, reset, 220.0f);
	}

	public static void drawVec4Control(String name, Vector4f values, float reset, float width) {
		ImGui.pushID(name);

		ImGui.columns(2);
		ImGui.setColumnWidth(0, width);
		ImGui.text(name);
		ImGui.nextColumn();

		ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0.0f, 4.0f);
		float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
		Vector2f buttonSize = new Vector2f(lineHeight + 3.0f, lineHeight);
		float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 4.0f) / 4.0f;

		ImGui.pushItemWidth(widthEach);
		ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1.0f);

		if (ImGui.button("X", buttonSize.x, buttonSize.y)) {
			values.x = reset;
		}
		ImGui.popStyleColor(3);

		ImGui.sameLine();
		ImFloat imFloat = new ImFloat(values.x);
		ImGui.inputFloat("##X", imFloat);
		ImGui.popItemWidth();
		ImGui.sameLine();

		ImGui.pushItemWidth(widthEach);
		ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);
		if (ImGui.button("Y", buttonSize.x, buttonSize.y)) {
			values.y = reset;
		}
		ImGui.popStyleColor(3);

		ImGui.sameLine();
		ImFloat imFloat2 = new ImFloat(values.y);
		ImGui.inputFloat("##y", imFloat2);
		ImGui.popItemWidth();
		ImGui.sameLine();

		ImGui.pushItemWidth(widthEach);
		ImGui.pushStyleColor(ImGuiCol.Button, 0.1f, 0.2f, 0.8f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.2f, 0.3f, 0.9f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.2f, 0.8f, 1.0f);
		if (ImGui.button("Z", buttonSize.x, buttonSize.y)) {
			values.z = reset;
		}
		ImGui.popStyleColor(3);

		ImGui.sameLine();
		ImFloat imFloat3 = new ImFloat(values.z);
		ImGui.inputFloat("##z", imFloat3);
		ImGui.popItemWidth();
		ImGui.sameLine();

		ImGui.pushItemWidth(widthEach);
		ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.8f, 0.1f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.9f, 0.2f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.8f, 0.1f, 1.0f);
		if (ImGui.button("W", buttonSize.x, buttonSize.y)) {
			values.w = reset;
		}
		ImGui.popStyleColor(3);

		ImGui.sameLine();
		ImFloat imFloat4 = new ImFloat(values.w);
		ImGui.inputFloat("##w", imFloat4);
		ImGui.popItemWidth();
		ImGui.sameLine();
		ImGui.nextColumn();

		values.x = imFloat.get();
		values.y = imFloat2.get();
		values.z = imFloat3.get();
		values.w = imFloat4.get();

		ImGui.popStyleVar();
		ImGui.columns(1);
		ImGui.dummy(0.0f, 1.0f);
		ImGui.popID();
	}

	public static boolean checkbox(String name, boolean value) {
		ImGui.pushID(name);

		ImGui.columns(2);
		ImGui.setColumnWidth(0, 220.0f);
		ImGui.text(name);
		ImGui.nextColumn();
		ImGui.dummy(0.0f, 1.0f);

		if (ImGui.checkbox("##" + name, value)) {
			value = !value;
		}
		ImGui.columns(1);
		ImGui.dummy(0.0f, 1.0f);
		ImGui.popID();

		return value;
	}

	public static float dragFloat(String name, float value) {
		ImGui.pushID(name);

		ImGui.columns(2);
		ImGui.setColumnWidth(0, 220.0f);
		ImGui.text(name);
		ImGui.nextColumn();

		ImFloat imFloat = new ImFloat(value);
		ImGui.inputFloat("##" + name, imFloat);
		ImGui.columns(1);
		ImGui.dummy(0.0f, 1.0f);
		ImGui.popID();

		return imFloat.get();
	}

	public static int dragInt(String name, int value) {
		ImGui.pushID(name);

		ImGui.columns(2);
		ImGui.setColumnWidth(0, 220.0f);
		ImGui.text(name);
		ImGui.nextColumn();

		ImInt imInt = new ImInt(value);
		ImGui.inputInt("##" + name, imInt);
		ImGui.columns(1);
		ImGui.dummy(0.0f, 1.0f);
		ImGui.popID();

		return imInt.get();
	}

	public static boolean colorPicker(String name, Vector4f color) {
		ImGui.pushID(name);

		ImGui.columns(2);
		ImGui.setColumnWidth(0, 220.0f);
		ImGui.text(name);
		ImGui.nextColumn();

		float[] col = {color.x, color.y, color.z, color.w};
		boolean res = ImGui.colorEdit4("##" + name, col);
		ImGui.columns(1);
		ImGui.dummy(0.0f, 1.0f);
		ImGui.popID();

		color.set(col[0], col[1], col[2], col[3]);
		return res;
	}

	public static String inputText(String name, String value) {
		ImGui.pushID(name);

		ImGui.columns(2);
		ImGui.setColumnWidth(0, 220.0f);
		ImGui.text(name);
		ImGui.nextColumn();

		ImString imString = new ImString(value, 256);
		if (ImGui.inputText("##" + name, imString)) {
			value = imString.get();
		}
		ImGui.columns(1);
		ImGui.dummy(0.0f, 1.0f);
		ImGui.popID();

		return value;
	}
}
