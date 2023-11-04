package engine.components;

import engine.editor.JImGui;
import engine.ruby.Window;
import engine.util.Prefabs;

public class QuestionBlock extends Block {
	private int amount = 1;
	private BlockType type = BlockType.COIN;

	@Override
	public void imGui() {
		type = JImGui.comboEnum("Type", type);
		if (type == BlockType.COIN) {
			amount = JImGui.dragInt("Amount", amount);
		}
	}

	@Override
	void playerHit(PlayerController player) {
		switch (type) {
			case COIN -> addCoin();
			case GROW -> addGrow();
			case FIRE -> addFire();
			case INVINCIBILITY -> addInvincibility();
		}
		StateMachine stateMachine = getGameObject().getComponent(StateMachine.class);
		if (stateMachine != null) {
			stateMachine.trigger("setInactive");
			setInactive();
		}
	}

	private void addCoin() {
		GameObject coin = Prefabs.generateCoinBlock();
		coin.addComponent(new CoinBlock(amount));
		coin.transform.setPosition(getGameObject().transform.getPosition());
		coin.transform.getPosition().add(0, 0.25f);
		Window.getScene().addGameObjectToScene(coin);
	}

	private void addGrow() {
		GameObject grow = Prefabs.generateMushroom();
		grow.transform.setPosition(getGameObject().transform.getPosition());
		grow.transform.getPosition().add(0, 0.25f);
		Window.getScene().addGameObjectToScene(grow);
	}

	private void addFire() {
		GameObject fire = Prefabs.generateFlower();
		fire.transform.setPosition(getGameObject().transform.getPosition());
		fire.transform.getPosition().add(0, 0.25f);
		Window.getScene().addGameObjectToScene(fire);
	}

	private void addInvincibility() {
		GameObject invincibility = Prefabs.generateStar();
		invincibility.transform.setPosition(getGameObject().transform.getPosition());
		invincibility.transform.getPosition().add(0, 0.25f);
		Window.getScene().addGameObjectToScene(invincibility);
	}

	private enum BlockType {
		COIN,
		GROW,
		FIRE,
		INVINCIBILITY,
	}
}
