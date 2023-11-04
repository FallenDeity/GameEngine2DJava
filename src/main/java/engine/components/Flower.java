package engine.components;

import engine.physics2d.components.RigidBody2D;
import engine.util.AssetPool;
import engine.util.CONSTANTS;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class Flower extends Component {

	@Override
	public void update(float dt) {

	}

	@Override
	public void start() {
		RigidBody2D rb = gameObject.getComponent(RigidBody2D.class);
		AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "powerup_appears.ogg").play();
		rb.setIsSensor(true);
	}

	@Override
	public void beginCollision(GameObject obj, Contact contact, Vector2f normal) {
		PlayerController player = obj.getComponent(PlayerController.class);
		if (player != null) {
			if (player.isFire()) {
				AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "coin.ogg").play();
			} else {
				player.addPowerUp(this);
			}
			contact.setEnabled(false);
			gameObject.destroy();
		}
	}
}
