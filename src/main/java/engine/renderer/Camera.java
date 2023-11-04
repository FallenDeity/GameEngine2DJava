package engine.renderer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Camera {
	public final Vector4f clearColor = new Vector4f(1.0f);
	private final Matrix4f projectionMatrix, inverseProjectionMatrix;
	private final Matrix4f viewMatrix, inverseViewMatrix;
	private final Vector2f position;
	private final Vector2f projectionSize = new Vector2f(6, 3);
	private float zoomAmount = 1.0f;

	public Camera(Vector2f position) {
		this.position = position;
		projectionMatrix = new Matrix4f();
		viewMatrix = new Matrix4f();
		inverseProjectionMatrix = new Matrix4f();
		inverseViewMatrix = new Matrix4f();
		adjustProjectionMatrix();
	}

	public void adjustProjectionMatrix() {
		projectionMatrix.identity();
		projectionMatrix.ortho(0.0f, projectionSize.x * zoomAmount, 0.0f, projectionSize.y * zoomAmount, 0.0f, 100.0f);
		projectionMatrix.invert(inverseProjectionMatrix);
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public Matrix4f getViewMatrix() {
		Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
		Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
		viewMatrix.identity();
		viewMatrix.lookAt(
				new Vector3f(position, 20.0f), cameraFront.add(position.x, position.y, 0.0f), cameraUp);
		viewMatrix.invert(inverseViewMatrix);
		return viewMatrix;
	}

	public void addZoom(float amount) {
		zoomAmount += amount;
	}

	public Matrix4f getInverseProjectionMatrix() {
		return inverseProjectionMatrix;
	}

	public Matrix4f getInverseViewMatrix() {
		return inverseViewMatrix;
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position.set(position);
	}

	public Vector2f getProjectionSize() {
		return projectionSize;
	}

	public float getZoom() {
		return zoomAmount;
	}

	public void setZoom(float amount) {
		zoomAmount = amount;
	}
}
