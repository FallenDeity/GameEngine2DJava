package engine.renderer;

import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer {
	private final int fboID, rboID;
	private final Texture texture;

	public FrameBuffer(int w, int h) {
		fboID = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fboID);
		texture = new Texture(w, h);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getID(), 0);
		rboID = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, rboID);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, w, h);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID);
		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			assert false : "ERROR::FRAMEBUFFER:: Framebuffer is not complete!";
			System.err.println("ERROR::FRAMEBUFFER:: Framebuffer is not complete!");
		}
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, fboID);
	}

	public void unbind() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public int getFboID() {
		return fboID;
	}

	public int getRboID() {
		return rboID;
	}

	public Texture getTexture() {
		return texture;
	}
}
