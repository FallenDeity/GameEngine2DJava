package engine.components;

import engine.ruby.KeyListener;
import engine.ruby.Window;
import engine.util.AssetPool;
import engine.util.CONSTANTS;
import engine.util.PipeDirection;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class Pipe extends Component {
	private final PipeDirection direction;
	private String connectedTo;
	private boolean isEntrance;
	private transient GameObject connectedPipe = null;
	private transient PlayerController player = null;

	public Pipe(PipeDirection direction) {
		this.direction = direction;
	}

	@Override
	public void update(float dt) {
		if (player == null || connectedPipe == null) return;
		boolean isPlayerInPipe = false;
		switch (direction) {
			case UP -> {
				if ((KeyListener.isKeyPressed(GLFW_KEY_DOWN) || KeyListener.isKeyPressed(GLFW_KEY_S)) && isEntrance && playerAtEntrance()) {
					isPlayerInPipe = true;
				}
			}
			case LEFT -> {
				System.out.println(playerAtEntrance());
				if ((KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)) && isEntrance && playerAtEntrance()) {
					isPlayerInPipe = true;
				}
			}
			case RIGHT -> {
				if ((KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) && isEntrance && playerAtEntrance()) {
					isPlayerInPipe = true;
				}
			}
			case DOWN -> {
				if ((KeyListener.isKeyPressed(GLFW_KEY_UP) || KeyListener.isKeyPressed(GLFW_KEY_W)) && isEntrance && playerAtEntrance()) {
					isPlayerInPipe = true;
				}
			}
		}
		if (isPlayerInPipe) {
			AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "pipe.ogg").play();
			player.setPosition(getPlayerPosition(connectedPipe));
			player.travelledPipe = !player.travelledPipe;
		}
	}

	@Override
	public void beginCollision(GameObject obj, Contact contact, Vector2f normal) {
		PlayerController player = obj.getComponent(PlayerController.class);
		if (player == null) return;
		this.player = player;
	}

	@Override
	public void endCollision(GameObject obj, Contact contact, Vector2f normal) {
		PlayerController player = obj.getComponent(PlayerController.class);
		if (player == null) return;
		this.player = null;
	}


	@Override
	public void start() {
		connectedPipe = Window.getScene().getGameObject(connectedTo);
	}

	private Vector2f getPlayerPosition(GameObject pipe) {
		Pipe pipeObj = connectedPipe.getComponent(Pipe.class);
		float entranceTolerance = 0.5f;
		switch (pipeObj.direction) {
			case UP -> {
				return new Vector2f(pipe.transform.getPosition()).add(0.0f, entranceTolerance);
			}
			case LEFT -> {
				return new Vector2f(pipe.transform.getPosition()).add(-entranceTolerance, 0.0f);
			}
			case RIGHT -> {
				return new Vector2f(pipe.transform.getPosition()).add(entranceTolerance, 0.0f);
			}
			case DOWN -> {
				return new Vector2f(pipe.transform.getPosition()).add(0.0f, -entranceTolerance);
			}
		}
		return new Vector2f();
	}

	private boolean playerAtEntrance() {
		if (player == null) return false;
		Vector2f min = new Vector2f(gameObject.transform.getPosition()).sub(new Vector2f(gameObject.transform.getScale()).mul(0.5f));
		Vector2f max = new Vector2f(gameObject.transform.getPosition()).add(new Vector2f(gameObject.transform.getScale()).mul(0.5f));
		Vector2f playerMin = new Vector2f(player.gameObject.transform.getPosition()).sub(new Vector2f(player.gameObject.transform.getScale()).mul(0.5f));
		Vector2f playerMax = new Vector2f(player.gameObject.transform.getPosition()).add(new Vector2f(player.gameObject.transform.getScale()).mul(0.5f));
		switch (direction) {
			case UP -> {
				return playerMin.y >= max.y &&
						playerMax.x > min.x &&
						playerMin.x < max.x;
			}
			case LEFT -> {
				return playerMin.x <= min.x &&
						playerMax.y > min.y &&
						playerMin.y < max.y;
			}
			case RIGHT -> {
				return playerMin.x >= max.x &&
						playerMax.y > min.y &&
						playerMin.y < max.y;
			}
			case DOWN -> {
				return playerMax.y <= min.y &&
						playerMax.x > min.x &&
						playerMin.x < max.x;
			}
		}
		return false;
	}
}
