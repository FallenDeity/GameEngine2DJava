package engine.renderer;

import engine.components.GameObject;
import engine.components.SpriteRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {
	private static final int MAX_BATCH_SIZE = 3000;
	private static Shader currShader = null;
	private final List<RenderBatch> batches;

	public Renderer() {
		this.batches = new ArrayList<>();
	}

	public static void bindShader(Shader shader) {
		currShader = shader;
	}

	public static Shader getCurrShader() {
		return currShader;
	}

	public void add(GameObject gb) {
		SpriteRenderer spr = gb.getComponent(SpriteRenderer.class);
		if (spr != null) {
			add(spr);
		}
	}

	private void add(SpriteRenderer spr) {
		boolean added = false;
		for (RenderBatch batch : batches) {
			if (batch.hasRoom() && batch.getZIndex() == spr.getGameObject().transform.getZIndex()) {
				if (spr.getTexture() == null
						|| (batch.hasTexture(spr.getTexture()) || batch.hasTextureRoom())) {
					batch.addSprite(spr);
					added = true;
					break;
				}
			}
		}
		if (!added) {
			RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, spr.getGameObject().transform.getZIndex(), this);
			newBatch.start();
			batches.add(newBatch);
			newBatch.addSprite(spr);
			Collections.sort(batches);
		}
	}

	public void render() {
		for (int i = 0; i < batches.size(); i++) {
			RenderBatch batch = batches.get(i);
			batch.render();
		}
	}

	public void destroyGameObject(GameObject gameObject) {
		if (gameObject.getComponent(SpriteRenderer.class) != null) {
			for (RenderBatch batch : batches) {
				if (batch.destroyIfExists(gameObject)) {
					return;
				}
			}
		}
	}
}
