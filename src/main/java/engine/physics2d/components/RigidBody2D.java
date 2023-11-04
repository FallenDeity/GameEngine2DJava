package engine.physics2d.components;

import engine.components.Component;
import engine.physics2d.enums.BodyType;
import engine.ruby.Window;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.joml.Vector2f;

public class RigidBody2D extends Component {
	private float friction = 0.1f;
	private float gravityScale = 1.0f;
	private float angularVelocity = 0.0f;
	private Vector2f velocity = new Vector2f();
	private float angularDamping = 0.8f;
	private float linearDamping = 0.9f;
	private float mass = 0.0f;
	private BodyType bodyType = BodyType.DYNAMIC;
	private boolean isSensor = false;
	private boolean fixedRotation = false;
	private boolean continuousCollision = true;
	private transient Body rawBody = null;

	public void setPosition(Vector2f pos) {
		if (rawBody == null) return;
		rawBody.setTransform(new Vec2(pos.x, pos.y), gameObject.transform.getRotation());
	}

	public void addForce(Vector2f velocity) {
		if (rawBody == null) return;
		rawBody.applyForceToCenter(new Vec2(velocity.x, velocity.y));
	}

	public void addImpulse(Vector2f velocity) {
		if (rawBody == null) return;
		rawBody.applyLinearImpulse(new Vec2(velocity.x, velocity.y), rawBody.getWorldCenter());
	}

	public float getFriction() {
		return friction;
	}

	public void setFriction(float friction) {
		this.friction = friction;
	}

	public boolean isSensor() {
		return isSensor;
	}

	public void setIsSensor(boolean isSensor) {
		this.isSensor = isSensor;
		if (rawBody != null) {
			Window.getPhysics2D().setIsSensor(this);
		}
	}

	public float getGravityScale() {
		return gravityScale;
	}

	public void setGravityScale(float gravityScale) {
		this.gravityScale = gravityScale;
		if (rawBody != null) {
			rawBody.setGravityScale(gravityScale);
		}
	}

	public float getAngularVelocity() {
		return angularVelocity;
	}

	public void setAngularVelocity(float angularVelocity) {
		this.angularVelocity = angularVelocity;
		if (rawBody != null) {
			rawBody.setAngularVelocity(angularVelocity);
		}
	}

	public float getAngularDamping() {
		return angularDamping;
	}

	public void setAngularDamping(float angularDamping) {
		this.angularDamping = angularDamping;
	}

	public float getLinearDamping() {
		return linearDamping;
	}

	public void setLinearDamping(float linearDamping) {
		this.linearDamping = linearDamping;
	}

	public float getMass() {
		return mass;
	}

	public void setMass(float mass) {
		this.mass = mass;
	}

	public BodyType getBodyType() {
		return bodyType;
	}

	public void setBodyType(BodyType bodyType) {
		this.bodyType = bodyType;
	}

	public boolean isFixedRotation() {
		return fixedRotation;
	}

	public void setFixedRotation(boolean fixedRotation) {
		this.fixedRotation = fixedRotation;
	}

	public boolean isContinuousCollision() {
		return continuousCollision;
	}

	public void setContinuousCollision(boolean continuousCollision) {
		this.continuousCollision = continuousCollision;
	}

	public Body getRawBody() {
		return rawBody;
	}

	public void setRawBody(Body rawBody) {
		this.rawBody = rawBody;
	}

	public Vector2f getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2f velocity) {
		this.velocity.set(velocity);
		if (rawBody != null) {
			rawBody.setLinearVelocity(new Vec2(velocity.x, velocity.y));
		}
	}

	@Override
	public void update(float dt) {
		if (rawBody != null) {
			if (bodyType == BodyType.DYNAMIC || bodyType == BodyType.KINEMATIC) {
				gameObject.transform.getPosition().set(rawBody.getPosition().x, rawBody.getPosition().y);
				gameObject.transform.setRotation((float) Math.toDegrees(rawBody.getAngle()));
				Vec2 vel = rawBody.getLinearVelocity();
				velocity = new Vector2f(vel.x, vel.y);
			} else {
				rawBody.setTransform(new Vec2(gameObject.transform.getPosition().x, gameObject.transform.getPosition().y), gameObject.transform.getRotation());
			}
		}
	}

	@Override
	public void start() {

	}
}
