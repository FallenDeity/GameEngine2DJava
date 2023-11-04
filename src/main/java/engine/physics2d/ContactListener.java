package engine.physics2d;

import engine.components.GameObject;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class ContactListener implements org.jbox2d.callbacks.ContactListener {
	@Override
	public void beginContact(Contact contact) {
		GameObject obj_A = (GameObject) contact.getFixtureA().getBody().getUserData();
		GameObject obj_B = (GameObject) contact.getFixtureB().getBody().getUserData();
		Vector2f normal = getNormal(contact, false), invNormal = getNormal(contact, true);
		obj_A.getComponents().forEach(c -> c.beginCollision(obj_B, contact, normal));
		obj_B.getComponents().forEach(c -> c.beginCollision(obj_A, contact, invNormal));
	}

	@Override
	public void endContact(Contact contact) {
		GameObject obj_A = (GameObject) contact.getFixtureA().getBody().getUserData();
		GameObject obj_B = (GameObject) contact.getFixtureB().getBody().getUserData();
		Vector2f normal = getNormal(contact, false), invNormal = getNormal(contact, true);
		obj_A.getComponents().forEach(c -> c.endCollision(obj_B, contact, normal));
		obj_B.getComponents().forEach(c -> c.endCollision(obj_A, contact, invNormal));
	}

	@Override
	public void preSolve(Contact contact, Manifold manifold) {
		GameObject obj_A = (GameObject) contact.getFixtureA().getBody().getUserData();
		GameObject obj_B = (GameObject) contact.getFixtureB().getBody().getUserData();
		Vector2f normal = getNormal(contact, false), invNormal = getNormal(contact, true);
		obj_A.getComponents().forEach(c -> c.preSolve(obj_B, contact, normal));
		obj_B.getComponents().forEach(c -> c.preSolve(obj_A, contact, invNormal));
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse contactImpulse) {
		GameObject obj_A = (GameObject) contact.getFixtureA().getBody().getUserData();
		GameObject obj_B = (GameObject) contact.getFixtureB().getBody().getUserData();
		Vector2f normal = getNormal(contact, false), invNormal = getNormal(contact, true);
		obj_A.getComponents().forEach(c -> c.postSolve(obj_B, contact, normal));
		obj_B.getComponents().forEach(c -> c.postSolve(obj_A, contact, invNormal));
	}

	public Vector2f getNormal(Contact contact, boolean negate) {
		WorldManifold worldManifold = new WorldManifold();
		contact.getWorldManifold(worldManifold);
		Vector2f normal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
		return negate ? new Vector2f(normal).negate() : normal;
	}
}
