package engine.renderer;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
	private final String filePath;
	private int shaderId;
	private boolean inUse = false;
	private String vertexShaderSource;
	private String fragmentShaderSource;

	public Shader(String filePath) {
		this.filePath = filePath;
		try {
			load();
		} catch (IOException e) {
			Logger logger = Logger.getLogger(Shader.class.getName());
			logger.severe(e.getMessage());
			assert false : "Error: Could not open file for shader: '" + filePath + "'";
		}
	}

	private void load() throws IOException {
		String source = new String(Files.readAllBytes(Paths.get(filePath)));
		String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");
		int index = source.indexOf("#type") + "#type".length();
		int eol = source.indexOf("\r\n", index);
		String firstPattern = source.substring(index, eol).trim();
		index = source.indexOf("#type", eol) + "#type".length();
		eol = source.indexOf("\r\n", index);
		String secondPattern = source.substring(index, eol).trim();
		readShader(splitString[1], firstPattern);
		readShader(splitString[2], secondPattern);
	}

	private void readShader(String source, String pattern) {
		switch (pattern) {
			case "vertex" -> vertexShaderSource = source;
			case "fragment" -> fragmentShaderSource = source;
			default -> {
				assert false : "Error: Shader type not supported: '" + pattern + "'";
			}
		}
	}

	public Shader compile() {
		int vertexShader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShader, vertexShaderSource);
		glCompileShader(vertexShader);
		if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
			int len = glGetShaderi(vertexShader, GL_INFO_LOG_LENGTH);
			System.err.printf("ERROR: '%s': vertex shader compilation failed\n", filePath);
			System.err.println(glGetShaderInfoLog(vertexShader, len));
			assert false : "";
		}

		int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShader, fragmentShaderSource);
		glCompileShader(fragmentShader);
		if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
			int len = glGetShaderi(fragmentShader, GL_INFO_LOG_LENGTH);
			System.err.printf("ERROR: '%s': fragment shader compilation failed\n", filePath);
			System.err.println(glGetShaderInfoLog(fragmentShader, len));
			assert false : "";
		}

		shaderId = glCreateProgram();
		glAttachShader(shaderId, vertexShader);
		glAttachShader(shaderId, fragmentShader);
		glLinkProgram(shaderId);
		if (glGetProgrami(shaderId, GL_LINK_STATUS) == GL_FALSE) {
			int len = glGetProgrami(shaderId, GL_INFO_LOG_LENGTH);
			System.err.printf("ERROR: '%s': shader program linking failed\n", filePath);
			System.err.println(glGetProgramInfoLog(shaderId, len));
			assert false : "";
		}
		System.out.println("Shader: " + filePath);
		System.out.println("OpenGL: " + glGetString(GL_VERSION));
		System.out.println("GLSL: " + glGetString(GL_SHADING_LANGUAGE_VERSION));
		System.out.println("Shader program: " + shaderId);
		return this;
	}

	public void use() {
		if (!inUse) {
			glUseProgram(shaderId);
			inUse = true;
		}
	}

	public void detach() {
		glUseProgram(0);
		inUse = false;
	}

	public void uploadMatrix4f(String name, Matrix4f matrix) {
		assert inUse : "Error: Shader not in use";
		int location = glGetUniformLocation(shaderId, name);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		matrix.get(buffer);
		glUniformMatrix4fv(location, false, buffer);
	}

	public void uploadMatrix3f(String name, Matrix4f matrix) {
		assert inUse : "Error: Shader not in use";
		int location = glGetUniformLocation(shaderId, name);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
		matrix.get(buffer);
		glUniformMatrix3fv(location, false, buffer);
	}

	public void uploadMatrix2f(String name, Matrix4f matrix) {
		assert inUse : "Error: Shader not in use";
		int location = glGetUniformLocation(shaderId, name);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
		matrix.get(buffer);
		glUniformMatrix2fv(location, false, buffer);
	}

	public void uploadVec4f(String name, Vector4f vector) {
		assert inUse : "Error: Shader not in use";
		int location = glGetUniformLocation(shaderId, name);
		glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
	}

	public void uploadFloat(String name, float value) {
		assert inUse : "Error: Shader not in use";
		int location = glGetUniformLocation(shaderId, name);
		glUniform1f(location, value);
	}

	public void uploadInt(String name, int value) {
		assert inUse : "Error: Shader not in use";
		int location = glGetUniformLocation(shaderId, name);
		glUniform1i(location, value);
	}

	public void uploadTexture(String name, int slot) {
		assert inUse : "Error: Shader not in use";
		int location = glGetUniformLocation(shaderId, name);
		glUniform1i(location, slot);
	}

	public void uploadIntArray(String name, int[] array) {
		assert inUse : "Error: Shader not in use";
		int location = glGetUniformLocation(shaderId, name);
		glUniform1iv(location, array);
	}
}
