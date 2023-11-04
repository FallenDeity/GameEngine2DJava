package engine.renderer;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Line2D {
	private final Vector2f from;
	private final Vector2f to;
	private final Vector3f color;
	private int lifetime;

	public Line2D(Vector2f from, Vector2f to, Vector3f color, int lifetime) {
		this.from = from;
		this.to = to;
		this.color = color;
		this.lifetime = lifetime;
	}

	public int beginFrame() {
		return --lifetime;
	}

	public Vector2f getFrom() {
		return from;
	}

	public Vector2f getTo() {
		return to;
	}

	public Vector3f getColor() {
		return color;
	}

}
