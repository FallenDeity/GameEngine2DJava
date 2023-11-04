package engine.renderer;

import engine.ruby.Window;
import engine.util.AssetPool;
import engine.util.CONSTANTS;
import engine.util.JMath;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {
	public final static int MAX_LINES = 100_000;
	private final static List<Line2D> lines = new ArrayList<>();
	private final static float[] vertices = new float[MAX_LINES * 6 * 2];
	private final static Shader shader = AssetPool.getShader(CONSTANTS.LINE2D_SHADER_PATH.getValue());
	private static int vaoID, vboID;
	private static boolean started = false;

	public static void start() {
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);

		vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

		glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
		glEnableVertexAttribArray(1);

		glLineWidth(2.0f);
	}

	public static void beginFrame() {
		if (!started) {
			start();
			started = true;
		}
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).beginFrame() < 0) {
				lines.remove(i);
				i--;
			}
		}
	}

	public static void draw() {
		if (lines.isEmpty()) return;
		int idx = 0;
		for (Line2D line : lines) {
			for (int i = 0; i < 2; i++) {
				Vector2f pos = i == 0 ? line.getFrom() : line.getTo();
				Vector3f color = line.getColor();
				vertices[idx++] = pos.x;
				vertices[idx++] = pos.y;
				vertices[idx++] = -10.0f;
				vertices[idx++] = color.x;
				vertices[idx++] = color.y;
				vertices[idx++] = color.z;
			}
		}
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertices, 0, lines.size() * 6 * 2));
		shader.use();
		shader.uploadMatrix4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
		shader.uploadMatrix4f("uView", Window.getScene().getCamera().getViewMatrix());
		glBindVertexArray(vaoID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glDrawArrays(GL_LINES, 0, lines.size());
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
		shader.detach();
	}

	public static void addLine(Vector2f from, Vector2f to) {
		addLine(from, to, new Vector3f(1, 0, 0), 1);
	}

	public static void addLine(Vector2f from, Vector2f to, Vector3f color) {
		addLine(from, to, color, 1);
	}

	public static void addLine(Vector2f from, Vector2f to, Vector3f color, int lifetime) {
		if (lines.size() >= MAX_LINES) return;
		lines.add(new Line2D(from, to, color, lifetime));
	}

	public static void addBox(Vector2f center, Vector2f size, float rotation) {
		addBox(center, size, rotation, new Vector3f(1, 0, 0), 1);
	}

	public static void addBox(Vector2f center, Vector2f size, float rotation, Vector3f color) {
		addBox(center, size, rotation, color, 1);
	}

	public static void addBox(Vector2f center, Vector2f size, float rotation, Vector3f color, int lifetime) {
		Vector2f min = new Vector2f(center).sub(new Vector2f(size).mul(0.5f));
		Vector2f max = new Vector2f(center).add(new Vector2f(size).mul(0.5f));
		Vector2f[] points = new Vector2f[]{
				new Vector2f(min.x, min.y),
				new Vector2f(max.x, min.y),
				new Vector2f(max.x, max.y),
				new Vector2f(min.x, max.y)
		};
		if (rotation != 0.0) {
			for (Vector2f v : points) {
				JMath.rotate(v, rotation, center);
			}
		}
		addLine(points[0], points[1], color, lifetime);
		addLine(points[1], points[2], color, lifetime);
		addLine(points[2], points[3], color, lifetime);
		addLine(points[3], points[0], color, lifetime);
	}

	public static void addCircle(Vector2f center, float radius) {
		addCircle(center, radius, new Vector3f(1, 0, 0), 1);
	}

	public static void addCircle(Vector2f center, float radius, Vector3f color) {
		addCircle(center, radius, color, 1);
	}

	public static void addCircle(Vector2f center, float radius, Vector3f color, int lifetime) {
		Vector2f prev = new Vector2f(center.x + radius, center.y);
		int MAX_SEGMENTS = 50;
		for (int i = 1; i <= MAX_SEGMENTS; i++) {
			float angle = i / (float) MAX_SEGMENTS * 360.0f;
			float x = center.x + radius * Math.cos(Math.toRadians(angle));
			float y = center.y + radius * Math.sin(Math.toRadians(angle));
			Vector2f next = new Vector2f(x, y);
			addLine(prev, next, color, lifetime);
			prev = next;
		}
	}

	public static void addPolygon(Vector2f[] points) {
		for (int i = 0; i < points.length; i++) {
			addLine(points[i], points[(i + 1) % points.length]);
		}
	}
}
