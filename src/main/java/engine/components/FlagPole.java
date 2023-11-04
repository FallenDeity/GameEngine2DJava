package engine.components;

import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class FlagPole extends Component {
	public final boolean top;

	public FlagPole(boolean top) {
		this.top = top;
	}

	@Override
	public void update(float dt) {

	}

	@Override
	public void start() {

	}

	@Override
	public void beginCollision(GameObject obj, Contact contact, Vector2f contactNormal) {
		PlayerController playerController = obj.getComponent(PlayerController.class);
		if (playerController != null) {
			playerController.playWinAnimation(this.gameObject);
		}
	}
}
