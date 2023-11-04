package engine.components;

import engine.physics2d.components.RigidBody2D;
import engine.renderer.Camera;
import engine.ruby.Window;
import engine.util.AssetPool;
import engine.util.CONSTANTS;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class TurtleAI extends Component {
	private final transient Vector2f velocity = new Vector2f();
	private final transient Vector2f acceleration = new Vector2f();
	private final transient Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);
	private transient boolean movingRight = false;
	private transient RigidBody2D rigidBody2D;
	private transient float walkSpeed = 0.6f;
	private transient boolean isGrounded = false;
	private transient boolean isDead = false;
	private transient boolean isMoving = false;
	private transient StateMachine stateMachine;
	private transient float debounceTimer = 0.32f;

	@Override
	public void update(float dt) {
		debounceTimer -= dt;
		Camera camera = Window.getScene().getCamera();
		if (gameObject.transform.getPosition().x > camera.getPosition().x + camera.getProjectionSize().x * camera.getZoom()) {
			return;
		}
		if (!isDead || isMoving) {
			if (movingRight) {
				gameObject.transform.setScale(new Vector2f(-0.25f, gameObject.transform.getScale().y));
				velocity.x = walkSpeed;
			} else {
				gameObject.transform.setScale(new Vector2f(0.25f, gameObject.transform.getScale().y));
				velocity.x = -walkSpeed;
			}
			acceleration.x = 0;
		} else {
			velocity.x = 0;
		}
		checkOnGround();
		if (isGrounded) {
			acceleration.y = 0.0f;
			velocity.y = 0.0f;
		} else {
			acceleration.y = Window.getPhysics2D().getGravity().y * 0.7f;
		}
		velocity.y += acceleration.y * dt;
		velocity.y = Math.max(Math.min(velocity.y, terminalVelocity.y), -terminalVelocity.y);
		rigidBody2D.setVelocity(velocity);
	}

	@Override
	public void start() {
		stateMachine = gameObject.getComponent(StateMachine.class);
		rigidBody2D = gameObject.getComponent(RigidBody2D.class);
		acceleration.y = Window.getPhysics2D().getGravity().y * 0.7f;
	}

	@Override
	public void preSolve(GameObject obj, Contact contact, Vector2f normal) {
		GoombaAI goomba = obj.getComponent(GoombaAI.class);
		if (isDead && isMoving && goomba != null) {
			goomba.stomp();
			contact.setEnabled(false);
			AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "kick.ogg").play();
		}
		PlayerController player = obj.getComponent(PlayerController.class);
		if (player != null) {
			if (!isDead && player.isNotDead() && !player.hurtOrInvincible() && normal.y > 0.58f) {
				player.enemyBounce();
				stomp();
				walkSpeed *= 3.0f;
			} else if (debounceTimer < 0 && player.isNotDead() && !player.hurtOrInvincible() && (isMoving || !isDead) && normal.y < 0.58f) {
				player.die();
				if (player.isNotDead()) {
					contact.setEnabled(false);
				}
			} else if (player.isNotDead() && !player.hurtOrInvincible()) {
				if (isDead && normal.y > 0.58f) {
					player.enemyBounce();
					isMoving = !isMoving;
					movingRight = normal.x < 0;
				} else if (isDead && !isMoving) {
					isMoving = true;
					movingRight = normal.x < 0;
					debounceTimer = 0.32f;
				}
			} else if (player.isNotDead() && player.hurtOrInvincible()) {
				contact.setEnabled(false);
			}
		} else if (Math.abs(normal.y) < 0.1f && !obj.isDestroyed()) {
			movingRight = normal.x < 0;
			if (isMoving && isDead) {
				AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "bump.ogg").play();
			}
		}
		if (obj.getComponent(Fireball.class) != null) {
			if (!isDead) {
				walkSpeed *= 3.0f;
				stomp();
			} else {
				isMoving = !isMoving;
				movingRight = normal.x < 0;
			}
			obj.getComponent(Fireball.class).destroy();
			contact.setEnabled(false);
		}
	}


	private void checkOnGround() {
		float innerWidth = 0.25f * 0.7f;
		isGrounded = Window.getPhysics2D().checkOnGround(gameObject, innerWidth, -0.2f);
	}

	private void stomp() {
		isDead = true;
		isMoving = false;
		velocity.zero();
		acceleration.zero();
		velocity.x = 0.0f;
		rigidBody2D.setVelocity(velocity);
		rigidBody2D.setAngularVelocity(0);
		stateMachine.trigger("spin");
		AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "bump.ogg").play();
	}
}
