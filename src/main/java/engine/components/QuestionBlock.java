package engine.components;

import engine.ruby.Window;
import engine.util.Prefabs;

public class QuestionBlock extends Block {
	private final BlockType type = BlockType.COIN;

	@Override
	void playerHit(PlayerController player) {
		switch (type) {
			case COIN -> addCoin(player);
			case GROW -> addGrow(player);
			case FIRE -> addFire(player);
			case INVINCIBILITY -> addInvincibility(player);
		}
		StateMachine stateMachine = getGameObject().getComponent(StateMachine.class);
		if (stateMachine != null) {
			stateMachine.trigger("setInactive");
			setInactive();
		}
	}

	private void addCoin(PlayerController player) {
		GameObject coin = Prefabs.generateCoinBlock();
		coin.transform.setPosition(getGameObject().transform.getPosition());
		coin.transform.getPosition().add(0, 0.25f);
		Window.getScene().addGameObjectToScene(coin);
	}

	private void addGrow(PlayerController player) {
		GameObject grow = Prefabs.generateMushroom();
		grow.transform.setPosition(getGameObject().transform.getPosition());
		grow.transform.getPosition().add(0, 0.25f);
		Window.getScene().addGameObjectToScene(grow);
	}

	private void addFire(PlayerController player) {
		GameObject fire = Prefabs.generateFlower();
		fire.transform.setPosition(getGameObject().transform.getPosition());
		fire.transform.getPosition().add(0, 0.25f);
		Window.getScene().addGameObjectToScene(fire);
	}

	private void addInvincibility(PlayerController player) {
		GameObject invincibility = Prefabs.generateStar();
		invincibility.transform.setPosition(getGameObject().transform.getPosition());
		invincibility.transform.getPosition().add(0, 0.25f);
		Window.getScene().addGameObjectToScene(invincibility);
	}

	private void addPowerup(PlayerController player) {
		if (player.isSmall()) {
			GameObject mushroom = Prefabs.generateMushroom();
			mushroom.transform.setPosition(getGameObject().transform.getPosition());
			mushroom.transform.getPosition().add(0, 0.25f);
			Window.getScene().addGameObjectToScene(mushroom);
		} else if (player.isBig()) {
			GameObject flower = Prefabs.generateFlower();
			flower.transform.setPosition(getGameObject().transform.getPosition());
			flower.transform.getPosition().add(0, 0.25f);
			Window.getScene().addGameObjectToScene(flower);
		} else if (player.isFire()) {
			GameObject star = Prefabs.generateStar();
			star.transform.setPosition(getGameObject().transform.getPosition());
			star.transform.getPosition().add(0, 0.25f);
			Window.getScene().addGameObjectToScene(star);
		} else if (player.isInvincible()) {
			addCoin(player);
		}
	}

	private enum BlockType {
		COIN,
		GROW,
		FIRE,
		INVINCIBILITY,
	}
}
