package engine.components;

import engine.renderer.Camera;
import engine.ruby.Window;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class GameCamera extends Component {
	private static boolean isUnderground = false;
	private final transient Camera gameCamera;
	private final Vector4f skyColor = new Vector4f(92.0f / 255.0f, 148.0f / 255.0f, 252.0f / 255.0f, 1.0f);
	private final Vector4f undergroundColor = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
	private final transient float cameraBuffer = 1.5f;
	private transient GameObject player;
	private transient float maxX = Float.MIN_VALUE, minX = Float.MAX_VALUE;
	private transient float undergroundY = 0;

	public GameCamera(Camera gameCamera) {
		this.gameCamera = gameCamera;
	}

	public static boolean isUnderground() {
		return isUnderground;
	}

	@Override
	public void update(float dt) {
		PlayerController controller = player.getComponent(PlayerController.class);
		if (player != null && !controller.hasWon() && controller.isNotDead()) {
			float xmin = Math.min(player.transform.getPosition().x + 2.5f, minX);
			float xmax = Math.max(player.transform.getPosition().x - 2.5f, maxX);
			float x = Math.clamp(player.transform.getPosition().x, xmin + gameCamera.getProjectionSize().x / 2.0f - cameraBuffer, xmax - gameCamera.getProjectionSize().x / 2.0f + cameraBuffer);
			gameCamera.setPosition(new Vector2f(x - 2.0f, gameCamera.getPosition().y));
			maxX = Math.max(maxX, xmax);
			minX = Math.min(minX, xmin);
			float playerBuffer = 0.25f;
			if (player.transform.getPosition().y < -playerBuffer) {
				gameCamera.setPosition(new Vector2f(player.transform.getPosition().x - 2.0f, undergroundY));
				gameCamera.clearColor.set(undergroundColor);
				isUnderground = true;
			} else {
				gameCamera.setPosition(new Vector2f(player.transform.getPosition().x - 2.0f, 0.0f));
				gameCamera.clearColor.set(skyColor);
				isUnderground = false;
			}
		}
	}

	@Override
	public void start() {
		player = Window.getScene().getGameObject(PlayerController.class);
		gameCamera.clearColor.set(skyColor);
		undergroundY = gameCamera.getPosition().y - gameCamera.getProjectionSize().y - cameraBuffer;
	}
}
