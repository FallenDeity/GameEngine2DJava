package engine.physics2d.components;

import engine.renderer.DebugDraw;
import org.joml.Vector2f;

public class Box2DCollider extends Collider {
	private Vector2f origin = new Vector2f();
	private Vector2f halfSize = new Vector2f(1.0f, 1.0f);

	public Vector2f getOrigin() {
		return origin;
	}

	public void setOrigin(Vector2f origin) {
		this.origin = origin;
	}

	public Vector2f getHalfSize() {
		return halfSize;
	}

	public void setHalfSize(Vector2f halfSize) {
		this.halfSize = halfSize;
	}

	@Override
	public void update(float dt) {

	}

	@Override
	public void editorUpdate(float dt) {
		Vector2f center = new Vector2f(gameObject.transform.getPosition()).add(getOffset());
		DebugDraw.addBox(center, halfSize, gameObject.transform.getRotation());
	}

	@Override
	public void start() {

	}
}
