package engine.components;

public class FontRenderer extends Component {
	@Override
	public void update(float dt) {
		System.out.println("FontRenderer update");
	}

	@Override
	public void start() {
		if (gameObject.getComponent(SpriteRenderer.class) != null) {
			System.out.println("FontRenderer start");
		}
	}
}
