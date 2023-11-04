package engine.components.fonts;

import org.joml.Vector2f;

public class CharInfo {
	public final int sourceX, sourceY;
	public final int width, height;
	private final Vector2f[] texCoords = new Vector2f[4];

	public CharInfo(int sourceX, int sourceY, int width, int height) {
		this.sourceX = sourceX;
		this.sourceY = sourceY;
		this.width = width;
		this.height = height;
	}

	public void calculateTexCoords(int textureWidth, int textureHeight) {
		float x0 = (float) sourceX / (float) textureWidth;
		float x1 = (float) (sourceX + width) / (float) textureWidth;
		float y0 = (float) (sourceY - height) / (float) textureHeight;
		float y1 = (float) sourceY / (float) textureHeight;
		texCoords[0] = new Vector2f(x1, y0);
		texCoords[1] = new Vector2f(x1, y1);
		texCoords[2] = new Vector2f(x0, y1);
		texCoords[3] = new Vector2f(x0, y0);
	}

	public Vector2f[] getTexCoords() {
		return texCoords;
	}
}
