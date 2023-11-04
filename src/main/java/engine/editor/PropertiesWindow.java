package engine.editor;

import engine.components.GameObject;
import engine.components.SpriteRenderer;
import engine.physics2d.components.Box2DCollider;
import engine.physics2d.components.CircleCollider;
import engine.physics2d.components.RigidBody2D;
import engine.renderer.Picker;
import imgui.ImGui;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class PropertiesWindow {
	private final List<Vector4f> objectColors;
	private final List<GameObject> gameObjects;
	private final Picker picker;

	public PropertiesWindow(Picker picker) {
		this.picker = picker;
		gameObjects = new ArrayList<>();
		objectColors = new ArrayList<>();
	}

	public void imGui() {
		if (gameObjects.size() == 1 && gameObjects.get(0) != null) {
			GameObject activeGameObject = gameObjects.get(0);
			ImGui.begin("Properties");
			if (ImGui.beginPopupContextWindow("Add Component")) {
				if (ImGui.menuItem("Add Rigidbody")) {
					if (activeGameObject.getComponent(RigidBody2D.class) == null) {
						activeGameObject.addComponent(new RigidBody2D());
					}
				}
				if (ImGui.menuItem("Add Box Collider")) {
					if (activeGameObject.getComponent(Box2DCollider.class) == null && activeGameObject.getComponent(CircleCollider.class) == null) {
						activeGameObject.addComponent(new Box2DCollider());
					}
				}
				if (ImGui.menuItem("Add Circle Collider")) {
					if (activeGameObject.getComponent(CircleCollider.class) == null && activeGameObject.getComponent(Box2DCollider.class) == null) {
						activeGameObject.addComponent(new CircleCollider());
					}
				}
				ImGui.endPopup();
			}
			activeGameObject.imGui();
			ImGui.end();
		}
	}

	public GameObject getActiveGameObject() {
		return gameObjects.size() == 1 ? gameObjects.get(0) : null;
	}

	public void setActiveGameObject(GameObject activeGameObject) {
		if (activeGameObject != null) {
			clearGameObjects();
			gameObjects.add(activeGameObject);
		}
	}

	public List<GameObject> getGameObjects() {
		return gameObjects;
	}

	public void clearGameObjects() {
		if (!objectColors.isEmpty()) {
			for (int i = 0; i < gameObjects.size(); i++) {
				SpriteRenderer spr = gameObjects.get(i).getComponent(SpriteRenderer.class);
				if (spr != null) {
					spr.setColor(objectColors.get(i));
				}
			}
		}
		gameObjects.clear();
		objectColors.clear();
	}

	public void addActiveGameObject(GameObject activeGameObject) {
		if (activeGameObject != null) {
			SpriteRenderer spr = activeGameObject.getComponent(SpriteRenderer.class);
			if (spr != null) {
				objectColors.add(new Vector4f(spr.getColor()));
				spr.setColor(new Vector4f(0.8f, 0.8f, 0.0f, 0.8f));
			} else {
				objectColors.add(new Vector4f());
			}
			gameObjects.add(activeGameObject);
		}
	}

	public Picker getPicker() {
		return picker;
	}
}
