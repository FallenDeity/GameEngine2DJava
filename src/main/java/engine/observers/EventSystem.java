package engine.observers;

import engine.components.GameObject;
import engine.observers.events.Event;

import java.util.ArrayList;
import java.util.List;

public class EventSystem {
	private static EventSystem instance = null;
	private final List<Observer> observers;

	private EventSystem() {
		observers = new ArrayList<>();
	}

	public static EventSystem getInstance() {
		if (instance == null) {
			instance = new EventSystem();
		}
		return instance;
	}

	public void addObserver(Observer observer) {
		observers.add(observer);
	}

	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}

	public void notify(GameObject gameObject, Event event) {
		observers.forEach(observer -> observer.onNotify(gameObject, event));
	}

}
