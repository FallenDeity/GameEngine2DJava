package engine.renderer;

import org.joml.Vector3f;

public class FontRenderer {
	private final FontBatch batch;

	public FontRenderer() {
		batch = new FontBatch();
	}

	public void write(String text, float x, float y, float scale, Vector3f color) {
		batch.addText(text, x, y, scale, color);
		batch.flushBatch();
	}
}
