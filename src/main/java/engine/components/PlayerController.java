package engine.components;

import engine.physics2d.components.PillboxCollider;
import engine.physics2d.components.RigidBody2D;
import engine.physics2d.enums.BodyType;
import engine.ruby.KeyListener;
import engine.ruby.Window;
import engine.scenes.Scenes;
import engine.util.AssetPool;
import engine.util.CONSTANTS;
import engine.util.Prefabs;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {
	private final Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);
	private transient final float bigJumpBoost = 1.05f;
	private transient final float playerWidth = 0.25f;
	private transient final Vector2f acceleration = new Vector2f();
	private transient final Vector2f velocity = new Vector2f();
	public transient boolean travelledPipe = false;
	private transient boolean isDead = false;
	private transient int enemyJump = 0;
	private float walkSpeed = 1.9f;
	private float jumpBoost = 1.0f;
	private float invincibleTime = 0.0f;
	private PlayerState previousState = null;
	private PlayerState playerState = PlayerState.SMALL;
	private transient boolean isGrounded = false;
	private transient float groundDebounce = 0.0f;
	private transient RigidBody2D rigidBody2D;
	private transient StateMachine stateMachine;
	private transient int jumpTime = 0;
	private transient float deadMaxHeight = 0.0f;
	private transient float deadMinHeight = 0.0f;
	private transient boolean deadGoingUp = true;
	private transient float blinkTime = 0.0f;
	private transient float deadTime = 0.0f;
	private transient SpriteRenderer spriteRenderer;
	private transient boolean playWinAnimation = false;
	private transient float timeToCastle = 4.5f;
	private transient float walkTime = 1.2f;
	private transient boolean playedFlagpoleSound = false;
	private transient float fireballDebounce = 0.0f;

	@Override
	public void update(float dt) {
		if (playWinAnimation) {
			if (spriteRenderer.getColor().w == 0) spriteRenderer.setColor(new Vector4f(1.0f));
			checkOnGround();
			if (!isGrounded) {
				gameObject.transform.setScale(new Vector2f(-playerWidth, gameObject.transform.getScale().y));
				gameObject.transform.getPosition().sub(0.0f, dt);
				stateMachine.trigger("stopRunning");
				stateMachine.trigger("stopJumping");
			} else {
				if (walkTime > 0) {
					gameObject.transform.setScale(new Vector2f(playerWidth, gameObject.transform.getScale().y));
					gameObject.transform.getPosition().add(walkSpeed * dt, 0.0f);
					stateMachine.trigger("startRunning");
				} else {
					stateMachine.trigger("stopRunning");
				}
				walkTime -= dt;
				timeToCastle -= dt;
				if (timeToCastle <= 0.0f) {
					Window.changeScene(Scenes.LEVEL_EDITOR);
					return;
				}
				if (!playedFlagpoleSound) {
					AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "stage_clear.ogg").play();
					playedFlagpoleSound = true;
				}
			}
			return;
		}
		if (isDead) {
			if (spriteRenderer.getColor().w == 0) spriteRenderer.setColor(new Vector4f(1.0f));
			deadTime -= dt;
			if (gameObject.transform.getPosition().y < deadMaxHeight && deadGoingUp) {
				gameObject.transform.getPosition().add(0.0f, walkSpeed / 2 * dt);
			} else if (gameObject.transform.getPosition().y >= deadMaxHeight && deadGoingUp) {
				deadGoingUp = false;
			} else if ((!deadGoingUp && gameObject.transform.getPosition().y > deadMinHeight) || (GameCamera.isUnderground() && gameObject.transform.getPosition().y > deadMinHeight - 4.5f)) {
				rigidBody2D.setBodyType(BodyType.KINEMATIC);
				acceleration.y = Window.getPhysics2D().getGravity().y * 0.7f;
				velocity.y += acceleration.y * dt;
				velocity.y = Math.max(Math.min(velocity.y, terminalVelocity.y), -terminalVelocity.y);
				rigidBody2D.setVelocity(velocity);
				rigidBody2D.setAngularVelocity(0.0f);
			} else if (gameObject.transform.getPosition().y <= deadMinHeight && !deadGoingUp && deadTime <= 0.0f) {
				deadTime = 0.0f;
				Window.changeScene(Scenes.LEVEL_SCENE);
			}
			return;
		}
		if (Window.getInstance().getTimer() <= 0.0f) {
			// die();
		}
		if ((gameObject.transform.getPosition().y < -5.0f && travelledPipe) || (gameObject.transform.getPosition().y < -0.5f && !travelledPipe)) {
			die();
		}
		if (invincibleTime > 0.0f) {
			invincibleTime -= dt;
			blinkTime -= dt;
			if (blinkTime <= 0.0f) {
				blinkTime = 0.1f;
				if (spriteRenderer.getColor().w == 1) {
					spriteRenderer.setColor(new Vector4f(1.0f, 1.0f, 1.0f, 0.0f));
				} else if (spriteRenderer.getColor().w == 0) {
					spriteRenderer.setColor(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
				}
			}
		} else {
			if (spriteRenderer.getColor().w == 0) spriteRenderer.setColor(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
			if (playerState == PlayerState.INVINCIBLE) {
				playerState = previousState;
				previousState = null;
				AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "invincible.ogg").stop();
				AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "main-theme-overworld.ogg").play();
			}
		}
		float damping = 0.05f;
		if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)) {
			gameObject.transform.setScale(new Vector2f(playerWidth, gameObject.transform.getScale().y));
			acceleration.x = walkSpeed;
			if (velocity.x < 0.0f) {
				stateMachine.trigger("switchDirection");
				velocity.x += damping;
			} else {
				stateMachine.trigger("startRunning");
			}
		} else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) {
			gameObject.transform.setScale(new Vector2f(-playerWidth, gameObject.transform.getScale().y));
			acceleration.x = -walkSpeed;
			if (velocity.x > 0.0f) {
				stateMachine.trigger("switchDirection");
				velocity.x -= damping;
			} else {
				stateMachine.trigger("startRunning");
			}
		} else {
			acceleration.x = 0.0f;
			if (velocity.x > 0) {
				velocity.x = Math.max(0.0f, velocity.x - damping);
			} else if (velocity.x < 0) {
				velocity.x = Math.min(0.0f, velocity.x + damping);
			}
			if (velocity.x == 0.0f) {
				stateMachine.trigger("stopRunning");
			}
		}
		fireballDebounce -= dt;
		if (KeyListener.isKeyPressed(GLFW_KEY_E) && playerState == PlayerState.FIRE && Fireball.canSpawn() && fireballDebounce <= 0.0f) {
			Vector2f add = gameObject.transform.getScale().x > 0 ? new Vector2f(0.26f, 0.0f) : new Vector2f(-0.26f, 0.0f);
			Vector2f position = new Vector2f(gameObject.transform.getPosition()).add(add);
			GameObject fireball = Prefabs.generateFireball(position);
			fireball.getComponent(Fireball.class).setMovingRight(gameObject.transform.getScale().x > 0);
			Window.getScene().addGameObjectToScene(fireball);
			AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "fireball.ogg").play();
			fireballDebounce = 0.5f;
		}
		checkOnGround();
		if (KeyListener.isKeyPressed(GLFW_KEY_UP) && (isGrounded || jumpTime > 0 || groundDebounce > 0)) {
			if ((isGrounded || groundDebounce > 0) && jumpTime == 0) {
				String sound = ((playerState == PlayerState.BIG || playerState == PlayerState.FIRE) ? "jump-super" : "jump-small") + ".ogg";
				AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + sound).play();
				jumpTime = 28;
				velocity.y = 3.0f;
			} else if (jumpTime > 0) {
				jumpTime--;
				velocity.y = (jumpTime / 2.2f) * jumpBoost;
			} else {
				velocity.y = 0;
			}
			groundDebounce = 0;
		} else if (enemyJump > 0) {
			enemyJump--;
			velocity.y = (enemyJump / 2.2f) * jumpBoost;
		} else if (!isGrounded) {
			if (jumpTime > 0) {
				velocity.y *= 0.35f;
				jumpTime = 0;
			}
			groundDebounce -= dt;
			acceleration.y = Window.getPhysics2D().getGravity().y * 0.7f;
		} else {
			velocity.y = 0;
			// acceleration.y = Window.getPhysics2D().getGravity().y * 0.7f;
			groundDebounce = 0.1f;
		}
		velocity.x += acceleration.x * dt;
		velocity.x = Math.max(Math.min(velocity.x, terminalVelocity.x), -terminalVelocity.x);
		velocity.y += acceleration.y * dt;
		velocity.y = Math.max(Math.min(velocity.y, terminalVelocity.y), -terminalVelocity.y);
		rigidBody2D.setVelocity(velocity);
		rigidBody2D.setAngularVelocity(0.0f);
		if (!isGrounded) {
			stateMachine.trigger("jump");
		} else {
			stateMachine.trigger("stopJumping");
		}
	}

	@Override
	public void start() {
		rigidBody2D = gameObject.getComponent(RigidBody2D.class);
		stateMachine = gameObject.getComponent(StateMachine.class);
		spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
		acceleration.y = Window.getPhysics2D().getGravity().y * 0.7f;
		rigidBody2D.setGravityScale(0.0f);
	}

	@Override
	public void beginCollision(GameObject other, Contact contact, Vector2f normal) {
		if (isDead) return;
		if (other.getComponent(Ground.class) != null) {
			if (Math.abs(normal.x) > 0.8f) {
				velocity.x = 0;
			} else if (Math.abs(normal.y) > 0.8f) {
				velocity.y = 0;
				// acceleration.y = 0;
				jumpTime = 0;
			}
		}
	}

	private void checkOnGround() {
		float innerWidth = playerWidth * 0.6f;
		float y = (playerState == PlayerState.SMALL || previousState == PlayerState.SMALL) ? -0.14f : -0.24f;
		isGrounded = Window.getPhysics2D().checkOnGround(gameObject, innerWidth, y);
	}

	private void buffPlayer() {
		gameObject.transform.setScale(new Vector2f(gameObject.transform.getScale().x, 0.42f));
		PillboxCollider collider = gameObject.getComponent(PillboxCollider.class);
		if (collider != null) {
			jumpBoost *= bigJumpBoost;
			walkSpeed *= bigJumpBoost;
			collider.setHeight(0.42f);
			collider.setWidth(0.25f);
			collider.getBottomCircle().setRadius(collider.getBottomCircle().getRadius() * bigJumpBoost);
		}
	}

	public boolean hasWon() {
		return playWinAnimation;
	}

	public void addPowerUp(Component component) {
		AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "powerup.ogg").play();
		if (component instanceof MushroomAI && playerState == PlayerState.SMALL) {
			playerState = PlayerState.BIG;
			buffPlayer();
			stateMachine.trigger("powerup");
		} else if (component instanceof Flower && playerState == PlayerState.SMALL) {
			playerState = PlayerState.FIRE;
			buffPlayer();
			stateMachine.trigger("powerup");
			stateMachine.trigger("powerup");
		} else if (component instanceof Flower && playerState == PlayerState.BIG) {
			playerState = PlayerState.FIRE;
			stateMachine.trigger("powerup");
		}
		if (component instanceof StarAI) {
			previousState = playerState;
			playerState = PlayerState.INVINCIBLE;
			invincibleTime += 15.0f;
			AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "main-theme-overworld.ogg").stop();
			AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "invincible.ogg").play();
		}
	}

	public void playWinAnimation(GameObject flagpole) {
		if (!playWinAnimation) {
			playWinAnimation = true;
			velocity.set(0.0f, 0.0f);
			acceleration.set(0.0f, 0.0f);
			rigidBody2D.setVelocity(velocity);
			rigidBody2D.setIsSensor(true);
			rigidBody2D.setBodyType(BodyType.STATIC);
			gameObject.transform.setPosition(new Vector2f(flagpole.transform.getPosition().x, gameObject.transform.getPosition().y));
			AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "main-theme-overworld.ogg").stop();
			AssetPool.getSound("assets/sounds/flagpole.ogg").play();
		}
	}

	public void die() {
		stateMachine.trigger("die");
		float hurtInvincibleTime = 1.5f;
		switch (playerState) {
			case SMALL -> {
				velocity.zero();
				acceleration.zero();
				rigidBody2D.setVelocity(new Vector2f());
				isDead = true;
				AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "main-theme-overworld.ogg").stop();
				AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "mario_die.ogg").play();
				deadMaxHeight = gameObject.transform.getPosition().y + 0.3f;
				rigidBody2D.setIsSensor(true);
				rigidBody2D.setBodyType(BodyType.STATIC);
				deadMinHeight = gameObject.transform.getPosition().y > 0.0f ? 0.0f : -0.25f;
				deadTime = 3f;
			}
			case BIG -> {
				playerState = PlayerState.SMALL;
				gameObject.transform.setScale(new Vector2f(gameObject.transform.getScale().x, 0.25f));
				PillboxCollider collider = gameObject.getComponent(PillboxCollider.class);
				if (collider != null) {
					jumpBoost /= bigJumpBoost;
					walkSpeed /= bigJumpBoost;
					collider.setHeight(0.25f);
					collider.setWidth(0.21f);
				}
				invincibleTime += hurtInvincibleTime;
				AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "pipe.ogg").play();
			}
			case FIRE -> {
				playerState = PlayerState.BIG;
				invincibleTime += hurtInvincibleTime;
				AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "pipe.ogg").play();
			}
		}
	}

	public boolean isFire() {
		return playerState == PlayerState.FIRE;
	}

	public boolean isSmall() {
		return playerState == PlayerState.SMALL;
	}

	public boolean isBig() {
		return playerState == PlayerState.BIG;
	}

	public boolean isInvincible() {
		return playerState == PlayerState.INVINCIBLE || invincibleTime > 0.0f || playWinAnimation;
	}

	public boolean isNotDead() {
		return !isDead && !playWinAnimation;
	}

	public boolean hurtOrInvincible() {
		return invincibleTime > 0.0f || playWinAnimation;
	}

	public void enemyBounce() {
		enemyJump = 8;
	}

	public void setPosition(Vector2f position) {
		gameObject.transform.setPosition(position);
		rigidBody2D.setPosition(position);
		velocity.zero();
		acceleration.zero();
		rigidBody2D.setVelocity(new Vector2f());
		rigidBody2D.setAngularVelocity(0.0f);
	}

	private enum PlayerState {
		SMALL,
		BIG,
		FIRE,
		INVINCIBLE
	}
}
