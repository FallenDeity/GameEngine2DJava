package engine.renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
	private String filePath;
	private transient int texID;
	private int width = -1, height = -1;

	public Texture() {

	}

	public Texture(int width, int height) {
		this.filePath = "Generated Texture";
		this.width = width;
		this.height = height;
		texID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
	}

	public void loadTexture(ByteBuffer image, int width, int height, int channels) {
		texID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		if (channels == 3) {
			glTexImage2D(
					GL_TEXTURE_2D,
					0,
					GL_RGB,
					width,
					height,
					0,
					GL_RGB,
					GL_UNSIGNED_BYTE,
					image);
		} else if (channels == 4) {
			glTexImage2D(
					GL_TEXTURE_2D,
					0,
					GL_RGBA,
					width,
					height,
					0,
					GL_RGBA,
					GL_UNSIGNED_BYTE,
					image);
		} else {
			assert false : "Error: (Texture) Unknown number of channels '" + channels + "'";
		}
	}

	public Texture loadTexture(String filePath) {
		this.filePath = filePath;
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		IntBuffer channels = BufferUtils.createIntBuffer(1);
		stbi_set_flip_vertically_on_load(true);
		ByteBuffer image = stbi_load(filePath, width, height, channels, 0);
		if (image != null) {
			this.width = width.get(0);
			this.height = height.get(0);
			loadTexture(image, this.width, this.height, channels.get(0));
		} else {
			assert false : "Error: (Texture) Could not load image '" + filePath + "'";
		}
		stbi_image_free(image);
		return this;
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, texID);
	}

	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public int getID() {
		return texID;
	}

	public String getFilePath() {
		return filePath;
	}

	public int getWidth() {
		assert width != -1 : "Error: (Texture) Width not initialized";
		return width;
	}

	public int getHeight() {
		assert height != -1 : "Error: (Texture) Height not initialized";
		return height;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Texture)) return false;
		Texture texture = (Texture) o;
		return texture.getWidth() == getWidth()
				&& texture.getID() == getID()
				&& texture.getHeight() == getHeight()
				&& texture.getFilePath().equals(getFilePath());
	}

	@Override
	public String toString() {
		return "Texture{"
				+ "filePath='"
				+ getFilePath()
				+ '\''
				+ ", texID="
				+ texID
				+ ", width="
				+ width
				+ ", height="
				+ height
				+ '}';
	}
}
