package engine.components;

import engine.components.sprites.SpriteSheet;
import engine.ruby.KeyListener;
import engine.ruby.Window;
import engine.scenes.Scene;
import engine.util.AssetPool;
import engine.util.CONSTANTS;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;

public class GizmoSystem extends Component {
	private Gizmo activeGizmo;

	public GizmoSystem(Scene scene, GameObject obj) {
		SpriteSheet gizmos = AssetPool.getSpriteSheet(CONSTANTS.GIZMOS_PATH.getValue(), 24, 48, 0, 3);
		obj.addComponent(new TranslateGizmo(scene, gizmos.getSprite(1), Window.getImGuiLayer().getPropertiesWindow()));
		obj.addComponent(new ScaleGizmo(scene, gizmos.getSprite(2), Window.getImGuiLayer().getPropertiesWindow()));
		activeGizmo = obj.getComponent(TranslateGizmo.class);
	}

	@Override
	public void update(float dt) {

	}

	@Override
	public void editorUpdate(float dt) {
		if (activeGizmo instanceof TranslateGizmo) {
			gameObject.getComponent(TranslateGizmo.class).setUse(true);
			gameObject.getComponent(ScaleGizmo.class).setUse(false);
		} else if (activeGizmo instanceof ScaleGizmo) {
			gameObject.getComponent(TranslateGizmo.class).setUse(false);
			gameObject.getComponent(ScaleGizmo.class).setUse(true);
		} else {
			gameObject.getComponent(TranslateGizmo.class).setUse(false);
			gameObject.getComponent(ScaleGizmo.class).setUse(false);
		}
		if (KeyListener.isKeyPressed(GLFW_KEY_T)) {
			activeGizmo = gameObject.getComponent(TranslateGizmo.class);
		} else if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
			activeGizmo = gameObject.getComponent(ScaleGizmo.class);
		}
	}

	@Override
	public void start() {

	}

	public boolean gizmoActive() {
		return activeGizmo.activeGameObject != null;
	}
}
