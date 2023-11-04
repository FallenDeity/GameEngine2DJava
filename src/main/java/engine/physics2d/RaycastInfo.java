package engine.physics2d;

import engine.components.GameObject;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2f;

public class RaycastInfo implements RayCastCallback {
	private final GameObject requestedGameObject;
	public Fixture fixture;
	public Vector2f point, normal;
	public float fraction;
	public boolean hit;
	public GameObject hitGameObject;

	public RaycastInfo(GameObject requestedGameObject) {
		fixture = null;
		point = new Vector2f();
		normal = new Vector2f();
		fraction = 0.0f;
		hit = false;
		hitGameObject = null;
		this.requestedGameObject = requestedGameObject;
	}

	@Override
	public float reportFixture(Fixture fixture, Vec2 vec2, Vec2 normal, float fraction) {
		if (!fixture.m_userData.equals(requestedGameObject)) {
			this.fixture = fixture;
			this.point = new Vector2f(vec2.x, vec2.y);
			this.normal = new Vector2f(normal.x, normal.y);
			this.fraction = fraction;
			this.hit = fraction != 0.0f;
			this.hitGameObject = (GameObject) fixture.m_userData;
			return fraction;
		}
		return 1;
	}
}
