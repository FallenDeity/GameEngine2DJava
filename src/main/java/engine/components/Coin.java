package engine.components;

import engine.ruby.Window;
import engine.util.AssetPool;
import engine.util.CONSTANTS;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class Coin extends Component {
	private Vector2f topY;
	private transient boolean animate = false;

	@Override
	public void start() {
		topY = new Vector2f(this.gameObject.transform.getPosition().y).add(0, 0.5f);
	}

	@Override
	public void update(float dt) {
		if (animate) {
			if (this.gameObject.transform.getPosition().y < topY.y) {
				this.gameObject.transform.getPosition().add(0, 1.4f * dt);
				this.gameObject.transform.getScale().sub((0.5f * dt) % -1.0f, 0.0f);
			} else {
				Window.getLevelScene().coins++;
				gameObject.destroy();
			}
		}
	}

	@Override
	public void beginCollision(GameObject obj, Contact contact, Vector2f normal) {
		if (obj.getComponent(PlayerController.class) != null) {
			AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "coin.ogg").play();
			animate = true;
			contact.setEnabled(false);
		}
	}
}
