package engine.util;

import com.google.gson.*;
import engine.components.Component;
import engine.components.GameObject;
import engine.components.Transform;

import java.lang.reflect.Type;

public class GameObjectDeserializer implements JsonDeserializer<GameObject> {

	@Override
	public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsob = json.getAsJsonObject();
		String name = jsob.get("name").getAsString();
		JsonArray components = jsob.getAsJsonArray("components");
		GameObject gameObject = new GameObject(name);
		for (JsonElement component : components) {
			gameObject.addComponent(context.deserialize(component, Component.class));
		}
		gameObject.transform = gameObject.getComponent(Transform.class);
		return gameObject;
	}
}
