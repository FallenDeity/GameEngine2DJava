package engine.util;

import engine.components.sprites.SpriteSheet;
import engine.renderer.Shader;
import engine.renderer.Sound;
import engine.renderer.Texture;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
	private static final Map<String, Shader> shaders = new HashMap<>();
	private static final Map<String, Texture> textures = new HashMap<>();
	private static final Map<String, SpriteSheet> spriteSheets = new HashMap<>();
	private static final Map<String, Sound> sounds = new HashMap<>();

	public static Shader getShader(String path) {
		File file = new File(path);
		String name = file.getAbsolutePath();
		if (shaders.containsKey(name)) {
			return shaders.get(name);
		}
		Shader shader = new Shader(name).compile();
		shaders.put(name, shader);
		return shader;
	}

	public static Texture getTexture(String path) {
		File file = new File(path);
		String name = file.getAbsolutePath();
		if (textures.containsKey(name)) {
			return textures.get(name);
		}
		Texture texture = new Texture().loadTexture(path);
		textures.put(name, texture);
		return texture;
	}

	public static Texture getTexture(String name, ByteBuffer image, int width, int height, int channels) {
		if (textures.containsKey(name)) {
			return textures.get(name);
		}
		Texture texture = new Texture();
		texture.loadTexture(image, width, height, channels);
		textures.put(name, texture);
		return texture;
	}

	public static SpriteSheet getSpriteSheet(String path, int w, int h, int s, int totalSprites) {
		File file = new File(path);
		String name = file.getAbsolutePath();
		if (spriteSheets.containsKey(name)) {
			return spriteSheets.get(name);
		}
		SpriteSheet sheet = new SpriteSheet(getTexture(path), w, h, s, totalSprites);
		spriteSheets.put(name, sheet);
		return sheet;
	}

	public static Sound getSound(String path) {
		return getSound(path, false);
	}

	public static Sound getSound(String path, boolean loop) {
		File file = new File(path);
		String name = file.getAbsolutePath();
		if (sounds.containsKey(name)) {
			return sounds.get(name);
		}
		Sound sound = new Sound(path, loop);
		sounds.put(name, sound);
		return sound;
	}

	public static Collection<Sound> getSounds() {
		return sounds.values();
	}
}
