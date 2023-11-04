package engine.components;

import engine.util.AssetPool;
import engine.util.CONSTANTS;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public abstract class Block extends Component {
	protected final float bopSpeed = 0.4f;
	private transient boolean bopGoingUp = true;
	private transient boolean isAnimating = false;
	private transient Vector2f bopStart, topBopLocation;
	private transient boolean active = true;

	abstract void playerHit(PlayerController player);


	@Override
	public void update(float dt) {
		if (isAnimating) {
			if (bopGoingUp) {
				if (gameObject.transform.getPosition().y < topBopLocation.y) {
					gameObject.transform.getPosition().add(0, bopSpeed * dt);
				} else {
					bopGoingUp = false;
				}
			} else {
				if (gameObject.transform.getPosition().y > bopStart.y) {
					gameObject.transform.getPosition().sub(0, bopSpeed * dt);
				} else {
					gameObject.transform.getPosition().set(gameObject.transform.getPosition().x, bopStart.y);
					bopGoingUp = true;
					isAnimating = false;
				}
			}
		}
	}

	@Override
	public void beginCollision(GameObject other, Contact contact, Vector2f normal) {
		PlayerController player = other.getComponent(PlayerController.class);
		if (active && player != null && normal.y < -0.8f) {
			isAnimating = true;
			AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "bump.ogg").play();
			playerHit(player);
		}
	}

	@Override
	public void start() {
		bopStart = new Vector2f(gameObject.transform.getPosition());
		topBopLocation = new Vector2f(bopStart).add(0, 0.02f);
	}

	public void setInactive() {
		active = false;
	}
}
