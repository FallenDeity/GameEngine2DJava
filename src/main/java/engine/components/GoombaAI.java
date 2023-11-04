package engine.components;

import engine.physics2d.components.RigidBody2D;
import engine.renderer.Camera;
import engine.ruby.Window;
import engine.util.AssetPool;
import engine.util.CONSTANTS;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class GoombaAI extends Component {
	private final transient Vector2f velocity = new Vector2f();
	private final transient Vector2f acceleration = new Vector2f();
	private final transient Vector2f terminalVelocity = new Vector2f(1.0f, 3.0f);
	private transient RigidBody2D rb;
	private transient StateMachine stateMachine;
	private transient boolean movingRight = false;
	private transient boolean isGrounded = false;
	private transient boolean isDead = false;
	private transient float killTime = 0.5f;

	public void checkOnGround() {
		float width = 0.25f * 0.7f;
		isGrounded = Window.getPhysics2D().checkOnGround(gameObject, width, -0.14f);
	}

	@Override
	public void update(float dt) {
		Camera camera = Window.getScene().getCamera();
		if (gameObject.transform.getPosition().x > camera.getPosition().x + camera.getProjectionSize().x * camera.getZoom()) {
			return;
		}
		if (isDead) {
			killTime -= dt;
			if (killTime <= 0.0f) gameObject.destroy();
			rb.setVelocity(new Vector2f(0.0f, Window.getPhysics2D().getGravity().y * 0.7f));
			return;
		}
		float walkSpeed = 0.6f;
		velocity.x = movingRight ? walkSpeed : -walkSpeed;
		checkOnGround();
		if (isGrounded) {
			acceleration.y = velocity.y = 0.0f;
		} else {
			acceleration.y = Window.getPhysics2D().getGravity().y * 0.7f;
		}
		velocity.y += acceleration.y * dt;
		velocity.y = Math.max(Math.min(velocity.y, terminalVelocity.y), -terminalVelocity.y);
		rb.setVelocity(velocity);
	}

	@Override
	public void start() {
		rb = gameObject.getComponent(RigidBody2D.class);
		stateMachine = gameObject.getComponent(StateMachine.class);
		acceleration.y = Window.getPhysics2D().getGravity().y * 0.7f;
	}

	@Override
	public void preSolve(GameObject obj, Contact contact, Vector2f normal) {
		if (isDead) return;
		PlayerController player = obj.getComponent(PlayerController.class);
		if (player != null) {
			if (player.isNotDead() && !player.hurtOrInvincible() && normal.y > 0.58f) {
				player.enemyBounce();
				stomp();
			} else if (player.isNotDead() && !player.isInvincible()) {
				player.die();
				if (player.isNotDead()) {
					contact.setEnabled(false);
				}
			} else if (player.isNotDead() && player.isInvincible()) {
				contact.setEnabled(false);
			}
		} else if (Math.abs(normal.y) < 0.1f) {
			movingRight = normal.x < 0.0f;
		}
		if (obj.getComponent(Fireball.class) != null) {
			stomp();
			obj.getComponent(Fireball.class).destroy();
		}
	}

	public void stomp() {
		isDead = true;
		velocity.zero();
		rb.setVelocity(new Vector2f(0.0f, 0.0f));
		rb.setAngularVelocity(0.0f);
		rb.setGravityScale(0.0f);
		stateMachine.trigger("squish");
		rb.setIsSensor(true);
		AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "bump.ogg").play();
		gameObject.transform.getPosition().add(0.0f, 0.3f);
	}
}
