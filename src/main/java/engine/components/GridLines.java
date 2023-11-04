package engine.components;

import engine.renderer.Camera;
import engine.renderer.DebugDraw;
import engine.ruby.Window;
import engine.util.CONSTANTS;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class GridLines extends Component {
	@Override
	public void update(float dt) {

	}

	@Override
	public void editorUpdate(float dt) {
		float width = CONSTANTS.GRID_WIDTH.getIntValue(), height = CONSTANTS.GRID_HEIGHT.getIntValue();
		Camera camera = Window.getScene().getCamera();
		Vector2f cameraPos = camera.getPosition();
		Vector2f projectionSize = Window.getScene().getCamera().getProjectionSize();
		float firstX = ((int) Math.floor(cameraPos.x / width)) * width;
		float firstY = ((int) Math.floor(cameraPos.y / height)) * height;
		int linesVt = (int) (projectionSize.x * camera.getZoom() / width) + 2;
		int linesHz = (int) (projectionSize.y * camera.getZoom() / height) + 2;
		float h = (int) (projectionSize.y * camera.getZoom()) + (5 * height);
		float w = (int) (projectionSize.x * camera.getZoom()) + (5 * width);
		Vector3f color = new Vector3f(0.7f, 0.7f, 0.7f);
		for (int i = 0; i < Math.max(linesVt, linesHz); i++) {
			float x = firstX + (i * width);
			float y = firstY + (i * height);
			if (i < linesVt) {
				DebugDraw.addLine(new Vector2f(x, firstY), new Vector2f(x, firstY + h), color);
			}
			if (i < linesHz) {
				DebugDraw.addLine(new Vector2f(firstX, y), new Vector2f(firstX + w, y), color);
			}
		}
	}

	@Override
	public void start() {

	}
}
