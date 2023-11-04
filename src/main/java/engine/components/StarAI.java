package engine.components;

import engine.physics2d.components.RigidBody2D;
import engine.util.AssetPool;
import engine.util.CONSTANTS;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Math;
import org.joml.Vector2f;

public class StarAI extends Component {
	private transient final Vector2f velocity = new Vector2f(2, 0);
	private transient boolean movingRight = true;
	private transient RigidBody2D rb;
	private transient boolean isHit = false;

	@Override
	public void update(float dt) {
		float maxSpeed = 1.5f;
		if (movingRight && Math.abs(rb.getVelocity().x) < maxSpeed) {
			rb.addForce(velocity);
		} else if (!movingRight && Math.abs(rb.getVelocity().x) < maxSpeed) {
			rb.addForce(new Vector2f(-velocity.x, velocity.y));
		}
	}

	@Override
	public void start() {
		rb = gameObject.getComponent(RigidBody2D.class);
		AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "powerup_appears.ogg").play();
	}

	@Override
	public void preSolve(GameObject obj, Contact contact, Vector2f normal) {
		if (obj.getComponent(PlayerController.class) != null) {
			contact.setEnabled(false);
			if (!isHit) {
				isHit = true;
				obj.getComponent(PlayerController.class).addPowerUp(this);
				gameObject.destroy();
			}
		}
		if (Math.abs(normal.y) < 0.1) {
			movingRight = normal.x < 0;
		}
	}
}
