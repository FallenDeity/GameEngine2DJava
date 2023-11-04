package engine.scenes;

import engine.components.GameCamera;
import engine.components.GameObject;
import engine.util.AssetPool;
import engine.util.CONSTANTS;

public class LevelScene extends Scene {

	public LevelScene() {
		GameObject gameUtils = new GameObject("GameUtils");
		gameUtils.addComponent(new GameCamera(getCamera()));
		addGameObjectToScene(gameUtils);
		AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "main-theme-overworld.ogg").play();
	}
}
