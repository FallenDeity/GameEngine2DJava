package engine.components.sprites;

import engine.util.AssetPool;

import java.util.ArrayList;
import java.util.List;

public class AnimationState {
	private final static Sprite sprite = new Sprite();
	public final List<Frame> frames = new ArrayList<>();

	public String name;
	private transient float frameTime = 0.0f;
	private transient int currentFrame = 0;
	private boolean loop = false;

	public AnimationState(String name) {
		this.name = name;
	}

	public void addFrame(Sprite sprite, float frameTime) {
		frames.add(new Frame(sprite, frameTime));
	}

	public boolean getLoop() {
		return loop;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	public void refresh() {
		for (Frame frame : frames) {
			frame.sprite.setTexture(AssetPool.getTexture(frame.sprite.getTexture().getFilePath()));
		}
	}

	public void update(float dt) {
		if (currentFrame < frames.size()) {
			frameTime -= dt;
			if (frameTime <= 0.0f) {
				if (currentFrame != frames.size() - 1 || loop) {
					currentFrame = (currentFrame + 1) % frames.size();
				}
				frameTime = frames.get(currentFrame).frameTime;
			}
		}
	}

	public Sprite getSprite() {
		if (currentFrame < frames.size()) {
			return frames.get(currentFrame).sprite;
		}
		return sprite;
	}
}
