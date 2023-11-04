package engine.renderer;

import engine.components.GameObject;
import engine.components.SpriteRenderer;
import engine.components.Transform;
import engine.ruby.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch> {
	private static final int POS_SIZE = 2;
	private static final int COL_SIZE = 4;
	private static final int TEX_SIZE = 2;
	private static final int TEXID_SIZE = 1;
	private static final int ENTITY_SIZE = 1;
	private static final int POS_OFFSET = 0;
	private static final int COL_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
	private static final int TEX_OFFSET = COL_OFFSET + COL_SIZE * Float.BYTES;
	private static final int TEXID_OFFSET = TEX_OFFSET + TEX_SIZE * Float.BYTES;
	private static final int ENTITY_OFFSET = TEXID_OFFSET + TEXID_SIZE * Float.BYTES;
	private static final int VERTEX_SIZE = POS_SIZE + COL_SIZE + TEX_SIZE + TEXID_SIZE + ENTITY_SIZE;
	private static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

	private final SpriteRenderer[] sprites;
	private final float[] vertices;
	private final int maxBatchSize;
	private final List<Texture> textureSlots;
	private final int zIndex;
	private final Renderer renderer;
	private int vaoID, vboID;
	private int numSprites = 0;
	private boolean hasRoom = true;

	public RenderBatch(int size, int zIndex, Renderer renderer) {
		this.renderer = renderer;
		this.zIndex = zIndex;
		sprites = new SpriteRenderer[size];
		maxBatchSize = size;
		vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];
		textureSlots = new ArrayList<>();
	}

	@Override
	public int compareTo(RenderBatch o) {
		return Integer.compare(zIndex, o.zIndex);
	}

	private void loadVertexProperties(int index) {
		SpriteRenderer sprite = sprites[index];
		int offset = index * 4 * VERTEX_SIZE;
		Vector4f color = sprite.getColor();
		int texID = sprite.getTexture() != null ? textureSlots.indexOf(sprite.getTexture()) + 1 : 0;
		Matrix4f transform = new Matrix4f().identity();
		if (sprite.getTransform().isRotated()) {
			transform.translate(sprite.getTransform().getPosition().x, sprite.getTransform().getPosition().y, 0.0f);
			transform.rotate((float) Math.toRadians(sprite.getTransform().getRotation()), 0.0f, 0.0f, 1.0f);
			transform.scale(sprite.getTransform().getScale().x, sprite.getTransform().getScale().y, 1.0f);
		}
		Vector2f[] texCoords = new Vector2f[]{
				new Vector2f(0.5f, 0.5f),
				new Vector2f(0.5f, -0.5f),
				new Vector2f(-0.5f, -0.5f),
				new Vector2f(-0.5f, 0.5f)
		};
		float xAdd, yAdd;
		for (int i = 0; i < 4; i++) {
			xAdd = texCoords[i].x;
			yAdd = texCoords[i].y;
			Transform tf = sprite.getTransform();
			Vector4f position = new Vector4f(tf.getPosition().x + (xAdd * tf.getScale().x),
					tf.getPosition().y + (yAdd * tf.getScale().y), 0.0f, 1.0f);
			if (sprite.getTransform().isRotated()) {
				position = new Vector4f(xAdd, yAdd, 0.0f, 1.0f).mul(transform);
			}
			vertices[offset] = position.x;
			vertices[offset + 1] = position.y;
			vertices[offset + 2] = color.x;
			vertices[offset + 3] = color.y;
			vertices[offset + 4] = color.z;
			vertices[offset + 5] = color.w;
			vertices[offset + 6] = sprite.getTexCoords()[i].x;
			vertices[offset + 7] = sprite.getTexCoords()[i].y;
			vertices[offset + 8] = texID;
			vertices[offset + 9] = sprite.getGameObject().getUid() + 1;
			offset += VERTEX_SIZE;
		}
	}

	public void addSprite(SpriteRenderer sprite) {
		int index = numSprites++;
		sprites[index] = sprite;
		if (sprite.getTexture() != null) {
			if (!textureSlots.contains(sprite.getTexture())) {
				textureSlots.add(sprite.getTexture());
			}
		}
		loadVertexProperties(index);
		hasRoom = numSprites < maxBatchSize;
	}

	public void start() {
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
		vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);
		int eboID = glGenBuffers();
		int[] indices = generateIndices();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

		glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, COL_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COL_OFFSET);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(2, TEX_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_OFFSET);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(3, TEXID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXID_OFFSET);
		glEnableVertexAttribArray(3);
		glVertexAttribPointer(4, ENTITY_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ENTITY_OFFSET);
		glEnableVertexAttribArray(4);
	}

	private int[] generateIndices() {
		int[] elements = new int[6 * maxBatchSize];
		for (int i = 0; i < maxBatchSize; i++) {
			loadElementIndices(elements, i);
		}
		return elements;
	}

	private void loadElementIndices(int[] elements, int index) {
		int offset = index * 6;
		int vertex = index * 4;
		elements[offset] = vertex + 3;
		elements[offset + 1] = vertex + 2;
		elements[offset + 2] = vertex;
		elements[offset + 3] = vertex;
		elements[offset + 4] = vertex + 2;
		elements[offset + 5] = vertex + 1;
	}

	public void render() {
		boolean rebufferData = false;
		for (int i = 0; i < numSprites; i++) {
			SpriteRenderer sprite = sprites[i];
			if (sprite.isDirty()) {
				if (!hasTexture(sprite.getTexture())) {
					renderer.destroyGameObject(sprite.getGameObject());
					renderer.add(sprite.getGameObject());
				} else {
					loadVertexProperties(i);
					sprite.setDirty(false);
					rebufferData = true;
				}
			}
			if (sprite.getTransform().getZIndex() != zIndex) {
				destroyIfExists(sprite.getGameObject());
				renderer.add(sprite.getGameObject());
				i--;
			}
		}
		if (rebufferData) {
			glBindBuffer(GL_ARRAY_BUFFER, vboID);
			glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
		}

		Shader shader = Renderer.getCurrShader();
		shader.use();
		shader.uploadMatrix4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
		shader.uploadMatrix4f("uView", Window.getScene().getCamera().getViewMatrix());
		for (int i = 0; i < textureSlots.size(); i++) {
			glActiveTexture(GL_TEXTURE0 + i + 1);
			textureSlots.get(i).bind();
		}
		shader.uploadIntArray("uTextures", new int[]{0, 1, 2, 3, 4, 5, 6, 7});

		glBindVertexArray(vaoID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glDrawElements(GL_TRIANGLES, numSprites * 6, GL_UNSIGNED_INT, 0);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
		textureSlots.forEach(Texture::unbind);
		shader.detach();
	}

	public boolean destroyIfExists(GameObject go) {
		SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
		if (spr != null) {
			for (int i = 0; i < numSprites; i++) {
				if (sprites[i].equals(spr)) {
					for (int j = i; j < numSprites - 1; j++) {
						sprites[j] = sprites[j + 1];
						sprites[j].setDirty(true);
					}
					numSprites--;
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasRoom() {
		return hasRoom;
	}

	public boolean hasTextureRoom() {
		return textureSlots.size() < 7;
	}

	public boolean hasTexture(Texture texture) {
		return textureSlots.contains(texture);
	}

	public int getZIndex() {
		return zIndex;
	}
}
