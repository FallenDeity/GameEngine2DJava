package engine.components;

import engine.util.AssetPool;
import engine.util.CONSTANTS;
import org.joml.Vector2f;

public class CoinBlock extends Component {
	private Vector2f topY;

	@Override
	public void update(float dt) {
		if (gameObject.transform.getPosition().y < topY.y) {
			gameObject.transform.getPosition().add(0, 1.4f * dt);
			gameObject.transform.getScale().sub((0.5f * dt) % -0.1f, 0.0f);
		} else {
			gameObject.destroy();
		}
	}

	@Override
	public void start() {
		topY = new Vector2f(getGameObject().transform.getPosition()).add(0, 0.5f);
		AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "coin.ogg").play();
	}
}
