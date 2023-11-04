package engine.components;

import engine.physics2d.components.RigidBody2D;
import engine.ruby.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class Fireball extends Component {
	private static int fireballCount = 0;
	private transient final Vector2f velocity = new Vector2f();
	private transient final Vector2f terminalVelocity = new Vector2f(3.1f, 2.1f);
	private final transient Vector2f acceleration = new Vector2f();
	private transient RigidBody2D rb;
	private transient boolean movingRight = true;
	private transient boolean isGrounded = false;
	private transient float lifeTime = 4.0f;

	public static boolean canSpawn() {
		return fireballCount < 3;
	}

	public void checkOnGround() {
		float width = 0.25f * 0.7f;
		isGrounded = Window.getPhysics2D().checkOnGround(gameObject, width, -0.09f);
	}

	@Override
	public void update(float dt) {
		lifeTime -= dt;
		if (lifeTime <= 0.0f) {
			destroy();
			return;
		}
		float speed = 1.7f;
		velocity.x = movingRight ? speed : -speed;
		checkOnGround();
		if (isGrounded) {
			acceleration.y = 1.5f;
			velocity.y = 2.5f;
		} else {
			acceleration.y = Window.getPhysics2D().getGravity().y * 0.7f;
		}
		velocity.y += acceleration.y * dt;
		velocity.y = Math.max(Math.min(velocity.y, terminalVelocity.y), -terminalVelocity.y);
		rb.setVelocity(velocity);
	}

	@Override
	public void start() {
		fireballCount++;
		rb = gameObject.getComponent(RigidBody2D.class);
		acceleration.y = Window.getPhysics2D().getGravity().y * 0.7f;
	}

	@Override
	public void beginCollision(GameObject obj, Contact contact, Vector2f normal) {
		if (Math.abs(normal.x) > 0.8f) {
			movingRight = normal.x < 0;
		}
	}

	@Override
	public void preSolve(GameObject obj, Contact contact, Vector2f normal) {
		if (obj.getComponent(Fireball.class) != null || obj.getComponent(PlayerController.class) != null) {
			contact.setEnabled(false);
		}
	}

	public void destroy() {
		fireballCount--;
		gameObject.destroy();
	}

	public void setMovingRight(boolean movingRight) {
		this.movingRight = movingRight;
	}
}
