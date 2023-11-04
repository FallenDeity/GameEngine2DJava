package engine.components.sprites;

import engine.renderer.Texture;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class SpriteSheet {
	private final int numSprites;
	private final Texture texture;
	private final List<Sprite> sprites;

	public SpriteSheet(
			Texture texture, int spriteWidth, int spriteHeight, int spacing, int totalSprites) {
		int count = 0;
		numSprites = totalSprites;
		this.texture = texture;
		this.sprites = new ArrayList<>();
		int currentX = 0, currentY = texture.getHeight() - spriteHeight;
		while (currentY >= 0) {
			while (currentX + spriteWidth <= texture.getWidth()) {
				if (count == totalSprites) break;
				Vector2f[] texCoords = generateTexCoords(currentX, currentY, spriteWidth, spriteHeight);
				Sprite sprite = new Sprite().spriteSetAttributes(texture, texCoords);
				sprite.setWidth(spriteWidth);
				sprite.setHeight(spriteHeight);
				sprites.add(sprite);
				currentX += spriteWidth + spacing;
				count++;
			}
			currentX = 0;
			currentY -= spriteHeight + spacing;
		}
	}

	private Vector2f[] generateTexCoords(int x, int y, int width, int height) {
		float x0 = (float) x / texture.getWidth();
		float y0 = (float) y / texture.getHeight();
		float x1 = (float) (x + width) / texture.getWidth();
		float y1 = (float) (y + height) / texture.getHeight();
		return new Vector2f[]{
				new Vector2f(x1, y1), new Vector2f(x1, y0), new Vector2f(x0, y0), new Vector2f(x0, y1)
		};
	}

	public Sprite getSprite(int index) {
		assert 0 <= index && getNumSprites() >= index : "Invalid Sprite Index";
		return sprites.get(index);
	}

	public int getNumSprites() {
		return numSprites;
	}
}
