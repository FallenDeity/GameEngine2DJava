package engine.components.sprites;

import engine.renderer.Texture;
import org.joml.Vector2f;

import java.util.Arrays;

public class Sprite {
	private Texture texture = null;
	private float width = 0, height = 0;
	private Vector2f[] texCoords =
			new Vector2f[]{
					new Vector2f(1, 1), new Vector2f(1, 0), new Vector2f(0, 0), new Vector2f(0, 1)
			};

	public Sprite spriteSetAttributes(Texture texture, Vector2f[] texCoords) {
		this.texture = texture;
		this.texCoords = texCoords;
		return this;
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public Vector2f[] getTexCoords() {
		return texCoords;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public int getTextureID() {
		return texture == null ? -1 : texture.getID();
	}

	@Override
	public String toString() {
		return "Sprite{"
				+ "width="
				+ width
				+ ", height="
				+ height
				+ ", texture="
				+ texture
				+ ", texCoords="
				+ Arrays.toString(texCoords)
				+ '}';
	}
}
