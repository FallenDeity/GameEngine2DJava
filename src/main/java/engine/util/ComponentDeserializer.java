package engine.util;

import com.google.gson.*;
import engine.components.Component;

import java.lang.reflect.Type;

public class ComponentDeserializer
		implements JsonSerializer<Component>, JsonDeserializer<Component> {
	@Override
	public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsob = json.getAsJsonObject();
		String type = jsob.get("type").getAsString();
		JsonElement properties = jsob.get("properties");
		try {
			return context.deserialize(properties, Class.forName(type));
		} catch (ClassNotFoundException e) {
			throw new JsonParseException("Unknown element type: " + type, e);
		}
	}

	@Override
	public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsob = new JsonObject();
		jsob.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
		jsob.add("properties", context.serialize(src, src.getClass()));
		return jsob;
	}
}
