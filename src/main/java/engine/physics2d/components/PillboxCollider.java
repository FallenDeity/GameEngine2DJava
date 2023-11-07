package engine.physics2d.components;

import engine.ruby.Window;
import org.joml.Vector2f;

public class PillboxCollider extends Collider {
	private final transient CircleCollider bottomCircle = new CircleCollider();
	private final transient Box2DCollider box = new Box2DCollider();
	public float width = 0.1f, height = 0.2f;
	private transient boolean reset = false;

	@Override
	public void update(float dt) {
		if (reset) resetFixtures();
	}

	@Override
	public void editorUpdate(float dt) {
		bottomCircle.editorUpdate(dt);
		box.editorUpdate(dt);
		process();
		update(dt);
	}

	@Override
	public void start() {
		bottomCircle.setGameObject(gameObject);
		box.setGameObject(gameObject);
		process();
	}

	public void setWidth(float width) {
		if (this.width == width) return;
		this.width = width;
		process();
		resetFixtures();
	}

	public void setHeight(float height) {
		if (this.height == height) return;
		this.height = height;
		process();
		resetFixtures();
	}

	public void resetFixtures() {
		if (Window.getPhysics2D().isLocked()) {
			reset = true;
			return;
		}
		reset = false;
		if (gameObject != null) {
			RigidBody2D rb = gameObject.getComponent(RigidBody2D.class);
			if (rb != null) {
				Window.getPhysics2D().resetPillboxCollider(rb, this);
			}
		}
	}

	public CircleCollider getBottomCircle() {
		return bottomCircle;
	}

	public Box2DCollider getBox() {
		return box;
	}

	public void process() {
		float radius = (width / 2.0f), boxHeight = height - radius;
		bottomCircle.setRadius(radius);
		bottomCircle.setOffset(new Vector2f(offset).sub(0, (height - radius * 2.0f) / 2.0f));
		box.setHalfSize(new Vector2f(width - 0.01f, boxHeight));
		box.setOffset(new Vector2f(offset).add(0, (height - boxHeight) / 2.0f));
	}
}
