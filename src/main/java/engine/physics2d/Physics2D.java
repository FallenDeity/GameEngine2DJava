package engine.physics2d;

import engine.components.GameObject;
import engine.components.Ground;
import engine.components.Transform;
import engine.physics2d.components.Box2DCollider;
import engine.physics2d.components.CircleCollider;
import engine.physics2d.components.PillboxCollider;
import engine.physics2d.components.RigidBody2D;
import engine.renderer.DebugDraw;
import engine.ruby.Window;
import engine.util.CONSTANTS;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.joml.Vector2f;

public class Physics2D {
	private final Vec2 gravity = new Vec2(0, -10.0f);
	private final World world = new World(gravity);
	private float accumulator = 0.0f;

	public Physics2D() {
		world.setContactListener(new ContactListener());
	}

	public void add(GameObject gameObject) {
		RigidBody2D rigidBody2D = gameObject.getComponent(RigidBody2D.class);
		if (rigidBody2D != null && rigidBody2D.getRawBody() == null) {
			Transform tf = gameObject.transform;
			BodyDef bodyDef = new BodyDef();
			bodyDef.angle = (float) Math.toRadians(tf.getRotation());
			bodyDef.angularVelocity = rigidBody2D.getAngularVelocity();
			bodyDef.linearVelocity.set(rigidBody2D.getVelocity().x, rigidBody2D.getVelocity().y);
			bodyDef.position.set(tf.getPosition().x, tf.getPosition().y);
			bodyDef.angularDamping = rigidBody2D.getAngularDamping();
			bodyDef.linearDamping = rigidBody2D.getLinearDamping();
			bodyDef.fixedRotation = rigidBody2D.isFixedRotation();
			bodyDef.bullet = rigidBody2D.isContinuousCollision();
			bodyDef.userData = rigidBody2D.getGameObject();
			bodyDef.gravityScale = rigidBody2D.getGravityScale();

			switch (rigidBody2D.getBodyType()) {
				case DYNAMIC -> bodyDef.type = BodyType.DYNAMIC;
				case KINEMATIC -> bodyDef.type = BodyType.KINEMATIC;
				case STATIC -> bodyDef.type = BodyType.STATIC;
			}

			Body body = world.createBody(bodyDef);
			body.m_mass = rigidBody2D.getMass();
			rigidBody2D.setRawBody(body);

			CircleCollider circleCollider = gameObject.getComponent(CircleCollider.class);

			if (circleCollider != null) {
				addCircleCollider(rigidBody2D, circleCollider);
			}

			Box2DCollider box2DCollider = gameObject.getComponent(Box2DCollider.class);

			if (box2DCollider != null) {
				addBox2DCollider(rigidBody2D, box2DCollider);
			}

			PillboxCollider pillboxCollider = gameObject.getComponent(PillboxCollider.class);

			if (pillboxCollider != null) {
				addPillboxCollider(rigidBody2D, pillboxCollider);
			}
		}
	}

	public RaycastInfo raycast(GameObject requestedGameObject, Vector2f point1, Vector2f point2) {
		RaycastInfo raycastInfo = new RaycastInfo(requestedGameObject);
		world.raycast(raycastInfo, new Vec2(point1.x, point1.y), new Vec2(point2.x, point2.y));
		return raycastInfo;
	}

	public void destroyGameObject(GameObject gameObject) {
		RigidBody2D rigidBody2D = gameObject.getComponent(RigidBody2D.class);
		if (rigidBody2D != null && rigidBody2D.getRawBody() != null) {
			world.destroyBody(rigidBody2D.getRawBody());
			rigidBody2D.setRawBody(null);
		}
	}

	public void update(float dt) {
		accumulator += dt;
		if (accumulator >= 0.0f) {
			float timeStep = 1.0f / 60.0f;
			accumulator -= timeStep;
			int velocityIterations = 10;
			int positionIterations = 3;
			world.step(timeStep, velocityIterations, positionIterations);
		}
	}

	public void resetCircleCollider(RigidBody2D rb, CircleCollider collider) {
		Body body = rb.getRawBody();
		if (body == null) return;
		int fixtureCount = fixtureListSize(body);
		for (int i = 0; i < fixtureCount; i++) {
			body.destroyFixture(body.getFixtureList());
		}
		addCircleCollider(rb, collider);
		body.resetMassData();
	}

	public void resetBox2DCollider(RigidBody2D rb, Box2DCollider collider) {
		Body body = rb.getRawBody();
		if (body == null) return;
		int fixtureCount = fixtureListSize(body);
		for (int i = 0; i < fixtureCount; i++) {
			body.destroyFixture(body.getFixtureList());
		}
		addBox2DCollider(rb, collider);
		body.resetMassData();
	}

	public void resetPillboxCollider(RigidBody2D rb, PillboxCollider collider) {
		Body body = rb.getRawBody();
		if (body == null) return;
		int fixtureCount = fixtureListSize(body);
		for (int i = 0; i < fixtureCount; i++) {
			body.destroyFixture(body.getFixtureList());
		}
		addPillboxCollider(rb, collider);
		body.resetMassData();
	}

	public void addPillboxCollider(RigidBody2D rb, PillboxCollider collider) {
		Body body = rb.getRawBody();
		assert body != null : "RigidBody2D does not have a raw body";
		addCircleCollider(rb, collider.getBottomCircle());
		addBox2DCollider(rb, collider.getBox());
	}

	public void addCircleCollider(RigidBody2D rb, CircleCollider collider) {
		Body body = rb.getRawBody();
		assert body != null : "RigidBody2D does not have a raw body";
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(collider.getRadius());
		circleShape.m_p.set(collider.getOffset().x, collider.getOffset().y);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circleShape;
		fixtureDef.friction = rb.getFriction();
		fixtureDef.isSensor = rb.isSensor();
		fixtureDef.density = 1;
		fixtureDef.userData = collider.getGameObject();
		body.createFixture(fixtureDef);
	}

	public void addBox2DCollider(RigidBody2D rb, Box2DCollider collider) {
		Body body = rb.getRawBody();
		assert body != null : "RigidBody2D does not have a raw body";
		Vector2f size = new Vector2f(collider.getHalfSize()).mul(0.5f);
		Vector2f offset = new Vector2f(collider.getOffset());
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox(size.x, size.y, new Vec2(offset.x, offset.y), 0.0f);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polygonShape;
		fixtureDef.density = 1;
		fixtureDef.userData = collider.getGameObject();
		fixtureDef.friction = rb.getFriction();
		fixtureDef.isSensor = rb.isSensor();
		body.createFixture(fixtureDef);
	}

	private int fixtureListSize(Body body) {
		int count = 0;
		for (Fixture fixture = body.getFixtureList(); fixture != null; fixture = fixture.getNext()) {
			count++;
		}
		return count;
	}

	public void setIsSensor(RigidBody2D rigidBody2D) {
		Body body = rigidBody2D.getRawBody();
		if (body == null) return;
		for (Fixture fixture = body.getFixtureList(); fixture != null; fixture = fixture.getNext()) {
			fixture.setSensor(rigidBody2D.isSensor());
		}
	}

	public boolean isLocked() {
		return world.isLocked();
	}

	public Vector2f getGravity() {
		return new Vector2f(world.getGravity().x, world.getGravity().y);
	}

	public boolean checkOnGround(GameObject gameObject, float innerWidth, float height) {
		Vector2f begin = new Vector2f(gameObject.transform.getPosition()).sub(innerWidth / 2.0f, 0.0f);
		Vector2f end = new Vector2f(begin).add(0.0f, height);
		RaycastInfo infoB = Window.getPhysics2D().raycast(gameObject, begin, end);
		Vector2f otherEnd = new Vector2f(end).add(innerWidth, 0.0f);
		Vector2f otherBegin = new Vector2f(begin).add(innerWidth, 0.0f);
		RaycastInfo infoE = Window.getPhysics2D().raycast(gameObject, otherBegin, otherEnd);
		if (CONSTANTS.DEBUG.getIntValue() == 1) {
			DebugDraw.addLine(otherBegin, otherEnd);
			DebugDraw.addLine(begin, end);
		}
		return (infoB.hit && infoB.hitGameObject != null && infoB.hitGameObject.getComponent(Ground.class) != null) || (infoE.hit && infoE.hitGameObject != null && infoE.hitGameObject.getComponent(Ground.class) != null);
	}
}
