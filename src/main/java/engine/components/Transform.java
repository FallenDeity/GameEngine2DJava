package engine.components;

import engine.editor.JImGui;
import org.joml.Vector2f;

public class Transform extends Component {
	private final Vector2f offset = new Vector2f();
	private final Vector2f position;
	private final Vector2f scale;
	private float rotation = 0.0f;
	private int zIndex = 0;

	public Transform() {
		position = new Vector2f().add(offset);
		scale = new Vector2f();
	}

	public Transform(Vector2f position, Vector2f scale) {
		this.position = position.add(offset);
		this.scale = scale;
	}

	@Override
	public void imGui() {
		gameObject.setName(JImGui.inputText("Name", gameObject.getName()));
		super.imGui();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Transform) {
			Transform transform = (Transform) obj;
			return position.equals(transform.position) && scale.equals(transform.scale) && rotation == transform.rotation && zIndex == transform.zIndex;
		}
		return false;
	}

	public Transform copy() {
		return new Transform(new Vector2f(position), new Vector2f(scale));
	}

	public void copy(Transform transform) {
		position.set(transform.position);
		scale.set(transform.scale);
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position.set(position).add(offset);
	}

	public Vector2f getScale() {
		return scale;
	}

	public void setScale(Vector2f scale) {
		this.scale.set(scale);
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public boolean isRotated() {
		return rotation != 0.0f;
	}

	public int getZIndex() {
		return zIndex;
	}

	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
	}

	public void setOffset(Vector2f offset) {
		this.offset.set(offset);
	}

	@Override
	public void update(float dt) {

	}

	@Override
	public void start() {

	}
}
