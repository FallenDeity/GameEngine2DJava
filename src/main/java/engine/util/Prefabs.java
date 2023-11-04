package engine.util;

import engine.components.*;
import engine.components.sprites.AnimationState;
import engine.components.sprites.Sprite;
import engine.components.sprites.SpriteSheet;
import engine.physics2d.components.Box2DCollider;
import engine.physics2d.components.CircleCollider;
import engine.physics2d.components.PillboxCollider;
import engine.physics2d.components.RigidBody2D;
import engine.physics2d.enums.BodyType;
import engine.scenes.Scene;
import org.joml.Vector2f;

public class Prefabs {
	public static GameObject generateSpriteObject(Sprite sprite, float width, float height) {
		GameObject obj = Scene.createGameObject("Sprite_Object_Generated");
		obj.transform.setScale(new Vector2f(width, height));
		SpriteRenderer spr = new SpriteRenderer().setSprite(sprite);
		obj.addComponent(spr);
		return obj;
	}

	public static GameObject generateMario() {
		SpriteSheet players = AssetPool.getSpriteSheet(CONSTANTS.SPRITE_SHEET_PATH.getValue(), 16, 16, 0, 26);
		SpriteSheet power_players = AssetPool.getSpriteSheet(CONSTANTS.POWER_SHEET_PATH.getValue(), 16, 32, 0, 42);
		GameObject obj = generateSpriteObject(players.getSprite(0), CONSTANTS.GRID_WIDTH.getIntValue(), CONSTANTS.GRID_HEIGHT.getIntValue());
		AnimationState run = new AnimationState("Run");
		run.addFrame(players.getSprite(0), 0.2f);
		run.addFrame(players.getSprite(2), 0.2f);
		run.addFrame(players.getSprite(3), 0.2f);
		run.addFrame(players.getSprite(2), 0.2f);
		run.setLoop(true);
		AnimationState switchDirection = new AnimationState("Switch Direction");
		switchDirection.addFrame(players.getSprite(4), 0.1f);
		switchDirection.setLoop(false);
		AnimationState idle = new AnimationState("Idle");
		idle.addFrame(players.getSprite(0), 0.1f);
		idle.setLoop(false);
		AnimationState jump = new AnimationState("Jump");
		jump.addFrame(players.getSprite(5), 0.1f);
		jump.setLoop(false);
		AnimationState die = new AnimationState("Die");
		die.addFrame(players.getSprite(6), 0.1f);
		die.setLoop(false);

		AnimationState bigRun = new AnimationState("BigRun");
		bigRun.addFrame(power_players.getSprite(0), 0.2f);
		bigRun.addFrame(power_players.getSprite(1), 0.2f);
		bigRun.addFrame(power_players.getSprite(2), 0.2f);
		bigRun.addFrame(power_players.getSprite(3), 0.2f);
		bigRun.addFrame(power_players.getSprite(2), 0.2f);
		bigRun.addFrame(power_players.getSprite(1), 0.2f);
		bigRun.setLoop(true);
		AnimationState bigSwitchDirection = new AnimationState("Big Switch Direction");
		bigSwitchDirection.addFrame(power_players.getSprite(4), 0.1f);
		bigSwitchDirection.setLoop(false);
		AnimationState bigIdle = new AnimationState("BigIdle");
		bigIdle.addFrame(power_players.getSprite(0), 0.1f);
		bigIdle.setLoop(false);
		AnimationState bigJump = new AnimationState("BigJump");
		bigJump.addFrame(power_players.getSprite(5), 0.1f);
		bigJump.setLoop(false);

		int fireOffset = 21;
		AnimationState fireRun = new AnimationState("FireRun");
		fireRun.addFrame(power_players.getSprite(fireOffset), 0.2f);
		fireRun.addFrame(power_players.getSprite(fireOffset + 1), 0.2f);
		fireRun.addFrame(power_players.getSprite(fireOffset + 2), 0.2f);
		fireRun.addFrame(power_players.getSprite(fireOffset + 3), 0.2f);
		fireRun.addFrame(power_players.getSprite(fireOffset + 2), 0.2f);
		fireRun.addFrame(power_players.getSprite(fireOffset + 1), 0.2f);
		fireRun.setLoop(true);
		AnimationState fireSwitchDirection = new AnimationState("Fire Switch Direction");
		fireSwitchDirection.addFrame(power_players.getSprite(fireOffset + 4), 0.1f);
		fireSwitchDirection.setLoop(false);
		AnimationState fireIdle = new AnimationState("FireIdle");
		fireIdle.addFrame(power_players.getSprite(fireOffset), 0.1f);
		fireIdle.setLoop(false);
		AnimationState fireJump = new AnimationState("FireJump");
		fireJump.addFrame(power_players.getSprite(fireOffset + 5), 0.1f);
		fireJump.setLoop(false);

		StateMachine sm = new StateMachine();
		sm.addState(run);
		sm.addState(run);
		sm.addState(idle);
		sm.addState(switchDirection);
		sm.addState(jump);
		sm.addState(die);

		sm.addState(bigRun);
		sm.addState(bigIdle);
		sm.addState(bigSwitchDirection);
		sm.addState(bigJump);

		sm.addState(fireRun);
		sm.addState(fireIdle);
		sm.addState(fireSwitchDirection);
		sm.addState(fireJump);

		sm.setDefaultState(idle.name);

		sm.addState(run.name, switchDirection.name, "switchDirection");
		sm.addState(run.name, idle.name, "stopRunning");
		sm.addState(run.name, jump.name, "jump");
		sm.addState(switchDirection.name, idle.name, "stopRunning");
		sm.addState(switchDirection.name, run.name, "startRunning");
		sm.addState(switchDirection.name, jump.name, "jump");
		sm.addState(idle.name, run.name, "startRunning");
		sm.addState(idle.name, jump.name, "jump");
		sm.addState(jump.name, idle.name, "stopJumping");

		sm.addState(bigRun.name, bigSwitchDirection.name, "switchDirection");
		sm.addState(bigRun.name, bigIdle.name, "stopRunning");
		sm.addState(bigRun.name, bigJump.name, "jump");
		sm.addState(bigSwitchDirection.name, bigIdle.name, "stopRunning");
		sm.addState(bigSwitchDirection.name, bigRun.name, "startRunning");
		sm.addState(bigSwitchDirection.name, bigJump.name, "jump");
		sm.addState(bigIdle.name, bigRun.name, "startRunning");
		sm.addState(bigIdle.name, bigJump.name, "jump");
		sm.addState(bigJump.name, bigIdle.name, "stopJumping");

		sm.addState(fireRun.name, fireSwitchDirection.name, "switchDirection");
		sm.addState(fireRun.name, fireIdle.name, "stopRunning");
		sm.addState(fireRun.name, fireJump.name, "jump");
		sm.addState(fireSwitchDirection.name, fireIdle.name, "stopRunning");
		sm.addState(fireSwitchDirection.name, fireRun.name, "startRunning");
		sm.addState(fireSwitchDirection.name, fireJump.name, "jump");
		sm.addState(fireIdle.name, fireRun.name, "startRunning");
		sm.addState(fireIdle.name, fireJump.name, "jump");
		sm.addState(fireJump.name, fireIdle.name, "stopJumping");

		sm.addState(run.name, bigRun.name, "powerup");
		sm.addState(idle.name, bigIdle.name, "powerup");
		sm.addState(switchDirection.name, bigSwitchDirection.name, "powerup");
		sm.addState(jump.name, bigJump.name, "powerup");
		sm.addState(bigRun.name, fireRun.name, "powerup");
		sm.addState(bigIdle.name, fireIdle.name, "powerup");
		sm.addState(bigSwitchDirection.name, fireSwitchDirection.name, "powerup");
		sm.addState(bigJump.name, fireJump.name, "powerup");

		sm.addState(bigRun.name, run.name, "damage");
		sm.addState(bigIdle.name, idle.name, "damage");
		sm.addState(bigSwitchDirection.name, switchDirection.name, "damage");
		sm.addState(bigJump.name, jump.name, "damage");
		sm.addState(fireRun.name, bigRun.name, "damage");
		sm.addState(fireIdle.name, bigIdle.name, "damage");
		sm.addState(fireSwitchDirection.name, bigSwitchDirection.name, "damage");
		sm.addState(fireJump.name, bigJump.name, "damage");

		sm.addState(run.name, die.name, "die");
		sm.addState(switchDirection.name, die.name, "die");
		sm.addState(idle.name, die.name, "die");
		sm.addState(jump.name, die.name, "die");
		sm.addState(bigRun.name, run.name, "die");
		sm.addState(bigSwitchDirection.name, switchDirection.name, "die");
		sm.addState(bigIdle.name, idle.name, "die");
		sm.addState(bigJump.name, jump.name, "die");
		sm.addState(fireRun.name, bigRun.name, "die");
		sm.addState(fireSwitchDirection.name, bigSwitchDirection.name, "die");
		sm.addState(fireIdle.name, bigIdle.name, "die");
		sm.addState(fireJump.name, bigJump.name, "die");

		obj.addComponent(sm);
		PillboxCollider pb = new PillboxCollider();
		pb.setWidth(0.21f);
		pb.setHeight(0.25f);
		obj.addComponent(pb);
		RigidBody2D rb = new RigidBody2D();
		rb.setBodyType(BodyType.DYNAMIC);
		rb.setFixedRotation(true);
		rb.setMass(25.0f);
		obj.addComponent(rb);
		obj.addComponent(new PlayerController());
		obj.transform.setZIndex(10);

		return obj;
	}

	public static GameObject generateQuestionBlock() {
		SpriteSheet items = AssetPool.getSpriteSheet(CONSTANTS.ITEM_SHEET_PATH.getValue(), 16, 16, 0, 43);
		GameObject obj = generateSpriteObject(items.getSprite(0), CONSTANTS.GRID_WIDTH.getIntValue(), CONSTANTS.GRID_HEIGHT.getIntValue());
		AnimationState run = new AnimationState("Flicker");
		run.addFrame(items.getSprite(0), 0.57f);
		run.addFrame(items.getSprite(1), 0.57f);
		run.addFrame(items.getSprite(2), 0.57f);
		run.setLoop(true);
		AnimationState idle = new AnimationState("Inactive");
		idle.addFrame(items.getSprite(3), 0.1f);
		idle.setLoop(false);
		StateMachine sm = new StateMachine();
		sm.addState(run);
		sm.addState(idle);
		sm.setDefaultState(run.name);
		sm.addState(run.name, idle.name, "setInactive");
		obj.addComponent(sm);
		obj.addComponent(new QuestionBlock());
		RigidBody2D rb = new RigidBody2D();
		rb.setBodyType(BodyType.STATIC);
		obj.addComponent(rb);
		Box2DCollider bc = new Box2DCollider();
		bc.setHalfSize(new Vector2f(0.25f, 0.25f));
		obj.addComponent(bc);
		obj.addComponent(new Ground());
		return obj;
	}

	public static GameObject generateCoinBlock() {
		SpriteSheet items = AssetPool.getSpriteSheet(CONSTANTS.ITEM_SHEET_PATH.getValue(), 16, 16, 0, 43);
		GameObject obj = generateSpriteObject(items.getSprite(7), CONSTANTS.GRID_WIDTH.getIntValue(), CONSTANTS.GRID_HEIGHT.getIntValue());
		AnimationState coinFlip = new AnimationState("CoinFlip");
		coinFlip.addFrame(items.getSprite(7), 0.23f);
		coinFlip.addFrame(items.getSprite(8), 0.23f);
		coinFlip.addFrame(items.getSprite(9), 0.23f);
		coinFlip.setLoop(true);

		StateMachine sm = new StateMachine();
		sm.addState(coinFlip);
		sm.setDefaultState(coinFlip.name);
		obj.addComponent(sm);
		obj.addComponent(new QuestionBlock());
		return obj;
	}

	public static GameObject generateCoin() {
		SpriteSheet items = AssetPool.getSpriteSheet(CONSTANTS.ITEM_SHEET_PATH.getValue(), 16, 16, 0, 43);
		GameObject coin = generateSpriteObject(items.getSprite(7), 0.25f, 0.25f);

		AnimationState coinFlip = new AnimationState("CoinFlip");
		coinFlip.addFrame(items.getSprite(7), 0.57f);
		coinFlip.addFrame(items.getSprite(8), 0.23f);
		coinFlip.addFrame(items.getSprite(9), 0.23f);
		coinFlip.setLoop(true);

		StateMachine stateMachine = new StateMachine();
		stateMachine.addState(coinFlip);
		stateMachine.setDefaultState(coinFlip.name);
		coin.addComponent(stateMachine);
		coin.addComponent(new Coin());

		CircleCollider circleCollider = new CircleCollider();
		circleCollider.setRadius(0.12f);
		coin.addComponent(circleCollider);
		RigidBody2D rb = new RigidBody2D();
		rb.setIsSensor(true);
		rb.setBodyType(BodyType.STATIC);
		coin.addComponent(rb);

		return coin;
	}

	public static GameObject generateMushroom() {
		SpriteSheet items = AssetPool.getSpriteSheet(CONSTANTS.ITEM_SHEET_PATH.getValue(), 16, 16, 0, 43);
		GameObject obj = generateSpriteObject(items.getSprite(10), CONSTANTS.GRID_WIDTH.getIntValue(), CONSTANTS.GRID_HEIGHT.getIntValue());
		RigidBody2D rb = new RigidBody2D();
		rb.setBodyType(BodyType.DYNAMIC);
		rb.setFixedRotation(true);
		obj.addComponent(rb);
		CircleCollider cc = new CircleCollider();
		cc.setRadius(0.14f);
		obj.addComponent(cc);
		obj.addComponent(new MushroomAI());
		obj.transform.setZIndex(5);
		return obj;
	}

	public static GameObject generateFlower() {
		SpriteSheet items = AssetPool.getSpriteSheet(CONSTANTS.ITEM_SHEET_PATH.getValue(), 16, 16, 0, 43);
		GameObject obj = generateSpriteObject(items.getSprite(21), CONSTANTS.GRID_WIDTH.getIntValue(), CONSTANTS.GRID_HEIGHT.getIntValue());
		RigidBody2D rb = new RigidBody2D();
		rb.setBodyType(BodyType.STATIC);
		rb.setFixedRotation(true);
		rb.setContinuousCollision(false);
		obj.addComponent(rb);
		CircleCollider cc = new CircleCollider();
		cc.setRadius(0.14f);
		obj.addComponent(cc);
		obj.addComponent(new Flower());
		return obj;
	}

	public static GameObject generateStar() {
		SpriteSheet items = AssetPool.getSpriteSheet(CONSTANTS.ITEM_SHEET_PATH.getValue(), 16, 16, 0, 43);
		GameObject obj = generateSpriteObject(items.getSprite(24), CONSTANTS.GRID_WIDTH.getIntValue(), CONSTANTS.GRID_HEIGHT.getIntValue());
		AnimationState run = new AnimationState("StarRun");
		run.addFrame(items.getSprite(24), 0.23f);
		run.addFrame(items.getSprite(25), 0.23f);
		run.addFrame(items.getSprite(26), 0.23f);
		run.addFrame(items.getSprite(27), 0.23f);
		run.setLoop(true);
		StateMachine sm = new StateMachine();
		sm.addState(run);
		sm.setDefaultState(run.name);
		RigidBody2D rb = new RigidBody2D();
		rb.setBodyType(BodyType.DYNAMIC);
		rb.setFixedRotation(true);
		obj.addComponent(sm);
		obj.addComponent(rb);
		CircleCollider cc = new CircleCollider();
		cc.setRadius(0.14f);
		obj.addComponent(cc);
		obj.addComponent(new StarAI());
		obj.transform.setZIndex(5);
		return obj;
	}

	public static GameObject generateGoomba(boolean underworld) {
		int idx = underworld ? 20 : 14;
		SpriteSheet items = AssetPool.getSpriteSheet(CONSTANTS.SPRITE_SHEET_PATH.getValue(), 16, 16, 0, 26);
		GameObject obj = generateSpriteObject(items.getSprite(idx), CONSTANTS.GRID_WIDTH.getIntValue(), CONSTANTS.GRID_HEIGHT.getIntValue());
		AnimationState walk = new AnimationState(underworld ? "GoombaUnderworld" : "Goomba");
		walk.addFrame(items.getSprite(idx), 0.23f);
		walk.addFrame(items.getSprite(idx + 1), 0.23f);
		walk.setLoop(true);

		AnimationState squish = new AnimationState(underworld ? "GoombaSquishUnderworld" : "GoombaSquish");
		squish.addFrame(items.getSprite(idx + 2), 0.23f);
		squish.setLoop(false);

		StateMachine sm = new StateMachine();
		sm.addState(walk);
		sm.addState(squish);
		sm.setDefaultState(walk.name);
		sm.addState(walk.name, squish.name, "squish");
		obj.addComponent(sm);

		RigidBody2D rb = new RigidBody2D();
		rb.setBodyType(BodyType.DYNAMIC);
		rb.setFixedRotation(true);
		rb.setMass(1.0f);
		obj.addComponent(rb);

		CircleCollider cc = new CircleCollider();
		cc.setRadius(0.12f);
		obj.addComponent(cc);
		obj.addComponent(new GoombaAI());
		obj.transform.setZIndex(5);
		return obj;
	}

	public static GameObject generatePipe(PipeDirection direction) {
		SpriteSheet pipes = AssetPool.getSpriteSheet(CONSTANTS.PIPES_SHEET_PATH.getValue(), 32, 32, 0, 4);
		int idx = direction == PipeDirection.DOWN ? 0 : direction == PipeDirection.UP ? 1 : direction == PipeDirection.RIGHT ? 2 : 3;
		GameObject obj = generateSpriteObject(pipes.getSprite(idx), CONSTANTS.GRID_WIDTH.getIntValue() * 2, CONSTANTS.GRID_HEIGHT.getIntValue() * 2);
		RigidBody2D rb = new RigidBody2D();
		rb.setBodyType(BodyType.STATIC);
		rb.setFixedRotation(true);
		rb.setContinuousCollision(true);
		obj.addComponent(rb);
		Box2DCollider bc = new Box2DCollider();
		bc.setHalfSize(new Vector2f(0.5f, 0.5f));
		obj.addComponent(bc);
		obj.addComponent(new Pipe(direction));
		obj.addComponent(new Ground());
		return obj;
	}

	public static GameObject generateTurtle() {
		SpriteSheet turtles = AssetPool.getSpriteSheet(CONSTANTS.TURTLE_SHEET_PATH.getValue(), 16, 24, 0, 4);
		GameObject obj = generateSpriteObject(turtles.getSprite(0), CONSTANTS.GRID_WIDTH.getIntValue(), CONSTANTS.GRID_HEIGHT.getIntValue() + 0.1f);
		AnimationState walk = new AnimationState("TurtleWalk");
		walk.addFrame(turtles.getSprite(0), 0.23f);
		walk.addFrame(turtles.getSprite(1), 0.23f);
		walk.setLoop(true);
		AnimationState spin = new AnimationState("TurtleSpin");
		spin.addFrame(turtles.getSprite(2), 0.1f);
		spin.addFrame(turtles.getSprite(3), 0.1f);
		spin.setLoop(true);
		StateMachine sm = new StateMachine();
		sm.addState(walk);
		sm.addState(spin);
		sm.setDefaultState(walk.name);
		sm.addState(walk.name, spin.name, "spin");
		obj.addComponent(sm);
		RigidBody2D rb = new RigidBody2D();
		rb.setBodyType(BodyType.DYNAMIC);
		rb.setMass(1.0f);
		rb.setFixedRotation(true);
		obj.addComponent(rb);
		CircleCollider cc = new CircleCollider();
		cc.setRadius(0.13f);
		cc.setOffset(new Vector2f(0.0f, -0.05f));
		obj.addComponent(cc);
		obj.transform.setOffset(new Vector2f(0.0f, 0.05f));
		obj.addComponent(new TurtleAI());
		obj.transform.setZIndex(5);
		return obj;
	}

	public static GameObject generateFlag(boolean top) {
		int idx = top ? 6 : 33;
		SpriteSheet items = AssetPool.getSpriteSheet(CONSTANTS.ITEM_SHEET_PATH.getValue(), 16, 16, 0, 43);
		GameObject obj = generateSpriteObject(items.getSprite(idx), CONSTANTS.GRID_WIDTH.getIntValue(), CONSTANTS.GRID_HEIGHT.getIntValue());
		RigidBody2D rb = new RigidBody2D();
		rb.setBodyType(BodyType.STATIC);
		rb.setFixedRotation(true);
		rb.setContinuousCollision(false);
		obj.addComponent(rb);
		Box2DCollider bc = new Box2DCollider();
		bc.setHalfSize(new Vector2f(0.01f, 0.25f));
		bc.setOffset(new Vector2f(-0.08f, 0.0f));
		obj.addComponent(bc);
		obj.addComponent(new FlagPole(top));
		obj.addComponent(new Ground());
		return obj;
	}

	public static GameObject generateFireball(Vector2f position) {
		SpriteSheet items = AssetPool.getSpriteSheet(CONSTANTS.ITEM_SHEET_PATH.getValue(), 16, 16, 0, 43);
		GameObject obj = generateSpriteObject(items.getSprite(32), 0.18f, 0.18f);
		obj.transform.setPosition(position);
		RigidBody2D rb = new RigidBody2D();
		rb.setBodyType(BodyType.DYNAMIC);
		rb.setFixedRotation(true);
		obj.addComponent(rb);
		CircleCollider cc = new CircleCollider();
		cc.setRadius(0.08f);
		obj.addComponent(cc);
		obj.addComponent(new Fireball());
		obj.transform.setZIndex(5);
		return obj;
	}
}
