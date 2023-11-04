package engine.physics2d.components;

import engine.components.Component;
import org.joml.Vector2f;

public abstract class Collider extends Component {
	protected Vector2f offset = new Vector2f();

	public Vector2f getOffset() {
		return offset;
	}

	public void setOffset(Vector2f offset) {
		this.offset = offset;
	}
}
