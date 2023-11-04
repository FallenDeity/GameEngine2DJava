package engine.components;

import engine.components.sprites.Sprite;
import engine.editor.PropertiesWindow;
import engine.ruby.MouseListener;
import engine.scenes.Scene;
import engine.util.CONSTANTS;
import org.joml.Vector2f;

public class TranslateGizmo extends Gizmo {

	public TranslateGizmo(Scene scene, Sprite sprite, PropertiesWindow window) {
		super(scene, sprite, window);
	}

	@Override
	public void editorUpdate(float dt) {
		if (activeGameObject != null) {
			Vector2f pos = MouseListener.getWorld();
			float width = CONSTANTS.GRID_WIDTH.getIntValue(), height = CONSTANTS.GRID_HEIGHT.getIntValue();
			if (xActive && !yActive) {
				float X = isSnapping ? (((int) Math.floor(pos.x / width)) * width) + width / 2.0f : activeGameObject.transform.getPosition().x - MouseListener.getWorldDX();
				activeGameObject.transform.getPosition().set(
						X,
						activeGameObject.transform.getPosition().y
				);
			} else if (yActive) {
				float Y = isSnapping ? (((int) Math.floor(pos.y / height)) * height) + height / 2.0f : activeGameObject.transform.getPosition().y - MouseListener.getWorldDY();
				activeGameObject.transform.getPosition().set(
						activeGameObject.transform.getPosition().x,
						Y
				);
			}
		}
		super.editorUpdate(dt);
	}
}
