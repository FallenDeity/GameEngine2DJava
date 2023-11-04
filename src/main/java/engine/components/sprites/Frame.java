package engine.components.sprites;

public class Frame {
	public final Sprite sprite;
	public float frameTime;

	public Frame(Sprite sprite, float frameTime) {
		this.sprite = sprite;
		this.frameTime = frameTime;
	}
}
