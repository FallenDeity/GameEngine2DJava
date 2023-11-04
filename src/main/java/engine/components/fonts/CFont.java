package engine.components.fonts;

import engine.renderer.Texture;
import engine.util.AssetPool;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class CFont {
	private final String filePath;
	private final int size;
	private final Map<Integer, CharInfo> charInfoMap;
	private Texture texture;

	public CFont(String filePath, int size) {
		this.filePath = filePath;
		this.size = size;
		this.charInfoMap = new HashMap<>();
		generateBitmap();
	}

	private Font registerFont() {
		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			Font font = Font.createFont(Font.TRUETYPE_FONT, new File(filePath));
			ge.registerFont(font);
			return font;
		} catch (IOException | FontFormatException e) {
			Logger.getLogger(CFont.class.getName()).warning("Failed to register font");
			return null;
		}
	}

	public void generateBitmap() {
		Font font = registerFont();
		assert font != null : "Font is null";
		font = new Font(font.getName(), Font.PLAIN, size);
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		g2d.setFont(font);
		FontMetrics metrics = g2d.getFontMetrics();
		int estimatedWidth = (int) Math.sqrt(font.getNumGlyphs()) * font.getSize() + 1;
		int width = 0, height = metrics.getHeight();
		int x = 0, y = (int) (metrics.getHeight() * 1.4f);
		for (int i = 0; i < font.getNumGlyphs(); i++) {
			if (font.canDisplay(i)) {
				CharInfo ch = new CharInfo(x, y, metrics.charWidth(i), metrics.getHeight());
				charInfoMap.put(i, ch);
				width = Math.max(width, x + metrics.charWidth(i));
				x += metrics.charWidth(i);
				if (x >= estimatedWidth) {
					x = 0;
					y += (int) (metrics.getHeight() * 1.4f);
					height += (int) (metrics.getHeight() * 1.4f);
				}
			}
		}
		height += (int) (metrics.getHeight() * 1.4f);
		g2d.dispose();
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g2d = image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(font);
		g2d.setColor(Color.WHITE);
		for (int i = 0; i < font.getNumGlyphs(); i++) {
			if (font.canDisplay(i)) {
				CharInfo ch = charInfoMap.get(i);
				ch.calculateTexCoords(width, height);
				g2d.drawString(String.valueOf((char) i), ch.sourceX, ch.sourceY);
			}
		}
		g2d.dispose();
		uploadTexture(image);
	}

	private void uploadTexture(BufferedImage img) {
		int[] pixels = new int[img.getWidth() * img.getHeight()];
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
		ByteBuffer buffer = ByteBuffer.allocateDirect(img.getWidth() * img.getHeight() * 4);
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				int pixel = pixels[y * img.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}
		buffer.flip();
		texture = AssetPool.getTexture(filePath.replace(".ttf", "") + ".png", buffer, img.getWidth(), img.getHeight(), 4);
		buffer.clear();
	}

	public CharInfo getCharInfo(int codePoint) {
		return charInfoMap.getOrDefault(codePoint, new CharInfo(0, 0, 0, 0));
	}

	public Texture getTexture() {
		return texture;
	}

	public int getTextureID() {
		return texture.getID();
	}
}
