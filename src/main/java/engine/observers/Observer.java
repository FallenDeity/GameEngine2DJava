package engine.observers;

import engine.components.GameObject;
import engine.observers.events.Event;

public interface Observer {
	void onNotify(GameObject gameObject, Event event);
}
