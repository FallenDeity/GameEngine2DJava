package engine.components;

import engine.editor.JImGui;
import imgui.ImGui;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Logger;

public abstract class Component {
	private static int ID_COUNTER = 0;
	protected transient GameObject gameObject = null;
	private int uid = -1;

	public static void setIdCounter(int idCounter) {
		ID_COUNTER = idCounter;
	}

	public abstract void update(float dt);

	public void editorUpdate(float dt) {
	}

	public abstract void start();

	public void destroy() {
	}

	public void beginCollision(GameObject other, Contact contact, Vector2f normal) {
	}

	public void endCollision(GameObject other, Contact contact, Vector2f normal) {
	}

	public void preSolve(GameObject other, Contact contact, Vector2f normal) {
	}

	public void postSolve(GameObject other, Contact contact, Vector2f normal) {
	}

	public void imGui() {
		try {
			Field[] fields = this.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (Modifier.isTransient(field.getModifiers())) continue;
				boolean isPrivate = Modifier.isPrivate(field.getModifiers());
				if (isPrivate) {
					field.setAccessible(true);
				}
				Class<?> type = field.getType();
				Object value = field.get(this);
				String name = field.getName();
				if (type == float.class) {
					float val = (float) value;
					field.set(this, JImGui.dragFloat(name, val));
				} else if (type == int.class) {
					int val = (int) value;
					field.set(this, JImGui.dragInt(name, val));
				} else if (type == boolean.class) {
					Boolean valueBoolean = (Boolean) value;
					field.set(this, JImGui.checkbox(name, valueBoolean));
				} else if (type == String.class) {
					ImString valueString = new ImString((String) value);
					field.set(this, JImGui.inputText(name, valueString.get()));
				} else if (type == Vector3f.class) {
					Vector3f val = (Vector3f) value;
					JImGui.drawVec3Control(name, val);
				} else if (type == Vector4f.class) {
					Vector4f val = (Vector4f) value;
					JImGui.drawVec4Control(name, val);
				} else if (type == Vector2f.class) {
					Vector2f val = (Vector2f) value;
					JImGui.drawVec2Control(name, val);
				} else if (type.isEnum()) {
					String[] enumValues = new String[type.getEnumConstants().length];
					ImInt index = new ImInt(0);
					for (int i = 0; i < type.getEnumConstants().length; i++) {
						enumValues[i] = type.getEnumConstants()[i].toString();
						if (enumValues[i].equals(value.toString())) {
							index.set(i);
						}
					}
					if (ImGui.combo(name, index, enumValues)) {
						field.set(this, type.getEnumConstants()[index.get()]);
					}
				}
				if (isPrivate) {
					field.setAccessible(false);
				}
			}
		} catch (IllegalAccessException e) {
			Logger logger = Logger.getLogger(Component.class.getName());
			logger.warning(e.getMessage());
		}
	}

	protected void generateUid() {
		uid = uid == -1 ? ID_COUNTER++ : uid;
	}

	public int getUid() {
		return uid;
	}

	public GameObject getGameObject() {
		return gameObject;
	}

	public void setGameObject(GameObject gameObject) {
		this.gameObject = gameObject;
	}
}
