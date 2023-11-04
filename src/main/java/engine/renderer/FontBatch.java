package engine.renderer;

import engine.components.fonts.CFont;
import engine.components.fonts.CharInfo;
import engine.ruby.Window;
import engine.util.AssetPool;
import engine.util.CONSTANTS;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL15;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class FontBatch {
	private final static int SIZE = 1000;
	private final static int VERTEX_SIZE = 7;
	private final static int[] indices = {
			0, 1, 3,
			1, 2, 3
	};
	private final static CFont font = new CFont(CONSTANTS.GAME_FONT_PATH.getValue(), 64);
	private final static Shader shader = AssetPool.getShader(CONSTANTS.FONT_SHADER_PATH.getValue());
	private final float[] vertices;
	private int vao;
	private int vbo;
	private int index = 0;


	public FontBatch() {
		vertices = new float[SIZE * VERTEX_SIZE];
		initBatch();
	}

	public void generateEbo() {
		int elementSize = SIZE * 3;
		int[] elementBuffer = new int[elementSize];
		for (int i = 0; i < elementSize; i++) {
			elementBuffer[i] = indices[(i % 6)] + ((i / 6) * 4);
		}
		int ebo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);
	}

	private void initBatch() {
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, Float.BYTES * VERTEX_SIZE * SIZE, GL_DYNAMIC_DRAW);
		generateEbo();
		int stride = 7 * Float.BYTES;
		glVertexAttribPointer(0, 2, GL_FLOAT, false, stride, 0);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 2 * Float.BYTES);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 5 * Float.BYTES);
		glEnableVertexAttribArray(2);
	}

	public void flushBatch() {
		shader.use();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, Float.BYTES * VERTEX_SIZE * SIZE, GL_DYNAMIC_DRAW);
		glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
		glActiveTexture(GL_TEXTURE0);
		font.getTexture().bind();
		shader.uploadTexture("uFontTexture", 0);
		shader.uploadMatrix4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
		shader.uploadMatrix4f("uView", Window.getScene().getCamera().getViewMatrix());
		glBindVertexArray(vao);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glDrawElements(GL_TRIANGLES, index * 6, GL_UNSIGNED_INT, 0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);
		font.getTexture().unbind();
		shader.detach();
		index = 0;
	}

	private void addChar(CharInfo ch, float x, float y, float scale, Vector3f color) {
		if (index >= SIZE - 4) {
			flushBatch();
			return;
		}
		Vector2f[] texCoords = ch.getTexCoords();
		int idx = index * VERTEX_SIZE;
		float w = ch.width * scale, h = ch.height * scale;
		vertices[idx] = x + w;
		vertices[idx + 1] = y + h;
		vertices[idx + 7] = x + w;
		vertices[idx + 8] = y;
		vertices[idx + 14] = x;
		vertices[idx + 15] = y;
		vertices[idx + 21] = x;
		vertices[idx + 22] = y + h;
		for (int i = 0; i < 4; i++) {
			vertices[idx + 2 + (i * VERTEX_SIZE)] = color.x;
			vertices[idx + 3 + (i * VERTEX_SIZE)] = color.y;
			vertices[idx + 4 + (i * VERTEX_SIZE)] = color.z;
		}
		vertices[idx + 5] = texCoords[0].x;
		vertices[idx + 6] = texCoords[0].y;
		vertices[idx + 12] = texCoords[1].x;
		vertices[idx + 13] = texCoords[1].y;
		vertices[idx + 19] = texCoords[2].x;
		vertices[idx + 20] = texCoords[2].y;
		vertices[idx + 26] = texCoords[3].x;
		vertices[idx + 27] = texCoords[3].y;
		index += 4;
	}

	public void addText(String text, float x, float y, float scale, Vector3f color) {
		for (char ch : text.toCharArray()) {
			CharInfo c = font.getCharInfo(ch);
			if (c.width == 0) continue;
			addChar(c, x, y, scale, color);
			x += c.width * scale;
		}
	}
}
