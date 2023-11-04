package engine.renderer;

import org.joml.Vector2i;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.*;

public class Picker {
	private final int pickFBO;

	public Picker(int w, int h) {
		pickFBO = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, pickFBO);
		int pickTexID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, pickTexID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, w, h, 0, GL_RGB, GL_FLOAT, 0);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, pickTexID, 0);
		glEnable(GL_TEXTURE_2D);
		int depthTexID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, depthTexID);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, w, h, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexID, 0);
		glReadBuffer(GL_NONE);
		glDrawBuffer(GL_COLOR_ATTACHMENT0);
		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			assert false : "ERROR::FRAMEBUFFER:: Framebuffer is not complete!";
			System.err.println("ERROR::FRAMEBUFFER:: Framebuffer is not complete!");
		} else {
			glBindTexture(GL_TEXTURE_2D, 0);
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
		}
	}

	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, pickFBO);
	}

	public void unbind() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public int readPixel(int x, int y) {
		glBindFramebuffer(GL_READ_FRAMEBUFFER, pickFBO);
		glReadBuffer(GL_COLOR_ATTACHMENT0);
		float[] pixel = new float[3];
		glReadPixels(x, y, 1, 1, GL_RGB, GL_FLOAT, pixel);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
		return (int) pixel[0] - 1;
	}

	public float[] readPixels(Vector2i start, Vector2i end) {
		glBindFramebuffer(GL_READ_FRAMEBUFFER, pickFBO);
		glReadBuffer(GL_COLOR_ATTACHMENT0);
		Vector2i size = new Vector2i(end).sub(start).absolute();
		float[] pixels = new float[size.x * size.y * 3];
		glReadPixels(start.x, start.y, size.x, size.y, GL_RGB, GL_FLOAT, pixels);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] -= 1;
		}
		return pixels;
	}
}
