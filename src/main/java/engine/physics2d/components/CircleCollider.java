package engine.physics2d.components;

import engine.renderer.DebugDraw;
import engine.ruby.Window;
import org.joml.Vector2f;

public class CircleCollider extends Collider {
	private float radius = 1.0f;
	private boolean resetFixtures = false;

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
		resetFixtures = true;
	}

	@Override
	public void setOffset(Vector2f offset) {
		super.setOffset(offset);
		resetFixtures = true;
	}

	@Override
	public void editorUpdate(float dt) {
		Vector2f center = new Vector2f(gameObject.transform.getPosition()).add(getOffset());
		DebugDraw.addCircle(center, radius);
		update(dt);
	}

	@Override
	public void update(float dt) {
		if (resetFixtures) {
			resetFixtures();
		}
	}

	@Override
	public void start() {

	}

	public void resetFixtures() {
		if (Window.getPhysics2D().isLocked()) {
			resetFixtures = true;
			return;
		}
		resetFixtures = false;
		if (gameObject != null) {
			RigidBody2D rb = gameObject.getComponent(RigidBody2D.class);
			if (rb != null) {
				Window.getPhysics2D().resetCircleCollider(rb, this);
			}
		}
	}
}
