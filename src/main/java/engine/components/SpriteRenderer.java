package engine.components;

import engine.components.sprites.Sprite;
import engine.editor.JImGui;
import engine.renderer.Texture;
import engine.util.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {
	private final Vector4f color = new Vector4f(1, 1, 1, 1);
	private Sprite sprite = new Sprite();
	private boolean laterallyInverted = false;
	private transient Transform lastTransform;
	private transient boolean isDirty = true;

	@Override
	public void update(float dt) {
		if (!gameObject.transform.equals(lastTransform)) {
			lastTransform.copy(gameObject.transform);
			isDirty = true;
		}
	}

	@Override
	public void editorUpdate(float dt) {
		update(dt);
	}

	@Override
	public void imGui() {
		if (JImGui.colorPicker("Color Picker", color)) {
			isDirty = true;
		}
		if (JImGui.checkbox("Invert Laterally", laterallyInverted)) {
			invertLaterally();
		}
	}

	@Override
	public void start() {
		if (sprite.getTexture() != null) {
			sprite.setTexture(AssetPool.getTexture(sprite.getTexture().getFilePath()));
		}
		lastTransform = gameObject.transform.copy();
	}

	public Vector4f getColor() {
		return color;
	}

	public void setColor(Vector4f color) {
		if (!this.color.equals(color)) {
			this.color.set(color);
			isDirty = true;
		}
	}

	public Texture getTexture() {
		return sprite.getTexture();
	}

	public void setTexture(Texture texture) {
		sprite.setTexture(texture);
		isDirty = true;
	}

	public Vector2f[] getTexCoords() {
		return sprite.getTexCoords();
	}

	public Transform getTransform() {
		return gameObject.transform;
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public SpriteRenderer setSprite(Sprite sprite) {
		this.sprite = sprite;
		isDirty = true;
		return this;
	}

	public void invertLaterally() {
		if (laterallyInverted) {
			sprite.spriteSetAttributes(sprite.getTexture(), new Vector2f[]{
					new Vector2f(sprite.getTexCoords()[0]),
					new Vector2f(sprite.getTexCoords()[1]),
					new Vector2f(sprite.getTexCoords()[2]),
					new Vector2f(sprite.getTexCoords()[3])
			});
			laterallyInverted = false;
		} else {
			sprite.spriteSetAttributes(sprite.getTexture(), new Vector2f[]{
					new Vector2f(sprite.getTexCoords()[3]),
					new Vector2f(sprite.getTexCoords()[2]),
					new Vector2f(sprite.getTexCoords()[1]),
					new Vector2f(sprite.getTexCoords()[0])
			});
			laterallyInverted = true;
		}
		isDirty = true;
	}
}
