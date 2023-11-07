package engine.scenes;

import engine.components.GameCamera;
import engine.components.GameObject;
import engine.components.sprites.SpriteSheet;
import engine.util.AssetPool;
import engine.util.CONSTANTS;
import engine.util.Prefabs;
import org.joml.Vector2f;

public class LevelScene extends Scene {
	private final GameObject coin;
	public int coins = 0;

	public LevelScene() {
		SpriteSheet items = AssetPool.getSpriteSheet(CONSTANTS.ITEM_SHEET_PATH.getValue(), 16, 16, 0, 43);
		coin = Prefabs.generateSpriteObject(items.getSprite(7), 0.2f, 0.2f);
		GameObject gameUtils = new GameObject("GameUtils");
		gameUtils.addComponent(new GameCamera(getCamera()));
		addGameObjectToScene(gameUtils);
		addGameObjectToScene(coin);
		AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "main-theme-overworld.ogg").play();
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		float x = getCamera().getPosition().x + 5.3f;
		float y = getCamera().getPosition().y + 2.825f;
		coin.transform.setPosition(new Vector2f(x, y));
	}
}
