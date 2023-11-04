package engine.components;

import engine.util.AssetPool;
import engine.util.CONSTANTS;

public class BreakableBrick extends Block {

	@Override
	void playerHit(PlayerController player) {
		if (!player.isSmall()) {
			AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "break_block.ogg").play();
			gameObject.destroy();
		}
	}
}
