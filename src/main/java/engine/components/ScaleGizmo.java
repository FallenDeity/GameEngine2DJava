package engine.components;

import engine.components.sprites.Sprite;
import engine.editor.PropertiesWindow;
import engine.ruby.MouseListener;
import engine.scenes.Scene;

public class ScaleGizmo extends Gizmo {
	public ScaleGizmo(Scene scene, Sprite sprite, PropertiesWindow window) {
		super(scene, sprite, window);
	}

	@Override
	public void editorUpdate(float dt) {
		if (activeGameObject != null) {
			if (xActive && !yActive) {
				activeGameObject.transform.getScale().set(
						activeGameObject.transform.getScale().x - MouseListener.getWorldDX(),
						activeGameObject.transform.getScale().y
				);
			}
			if (yActive && !xActive) {
				activeGameObject.transform.getScale().set(
						activeGameObject.transform.getScale().x,
						activeGameObject.transform.getScale().y - MouseListener.getWorldDY()
				);
			}
		}
		super.editorUpdate(dt);
	}
}
