package engine.scenes;

import engine.components.*;
import engine.components.sprites.Sprite;
import engine.components.sprites.SpriteSheet;
import engine.editor.JImGui;
import engine.physics2d.components.Box2DCollider;
import engine.physics2d.components.RigidBody2D;
import engine.physics2d.enums.BodyType;
import engine.renderer.Sound;
import engine.ruby.Window;
import engine.util.AssetPool;
import engine.util.CONSTANTS;
import engine.util.PipeDirection;
import engine.util.Prefabs;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LevelEditorScene extends Scene {
	private final SpriteSheet blocks;
	private final GameObject editor;

	public LevelEditorScene() {
		editor = createGameObject("Editor");
		editor.setNotSerializable();
		editor.addComponent(new MouseControls());
		editor.addComponent(new KeyControls());
		editor.addComponent(new GridLines());
		editor.addComponent(new EditorCamera(camera));
		editor.addComponent(new GizmoSystem(this, editor));
		blocks = AssetPool.getSpriteSheet(CONSTANTS.BLOCK_SHEET_PATH.getValue(), 16, 16, 0, 81);
		addGameObjectToScene(editor);
		AssetPool.getSound(CONSTANTS.SOUNDS_PATH.getValue() + "main-theme-overworld.ogg").stop();
	}

	public boolean gizmoActive() {
		GizmoSystem gizmoSystem = editor.getComponent(GizmoSystem.class);
		return gizmoSystem.gizmoActive();
	}

	private List<Sound> loadSounds() {
		String sound_dir = CONSTANTS.SOUNDS_PATH.getValue();
		File[] files = new File(sound_dir).listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile() && file.getName().endsWith(".ogg")) {
					String path = file.getAbsolutePath();
					AssetPool.getSound(path, false);
				}
			}
		}
		return new ArrayList<>(AssetPool.getSounds());
	}

	private void addBlock(SpriteSheet sheet, int i, ImVec2 windowSize, ImVec2 itemSpacing, float windowX, boolean decoration) {
		Sprite sprite = sheet.getSprite(i);
		float spriteWidth = sprite.getWidth() * 3, spriteHeight = sprite.getHeight() * 3;
		Vector2f[] texCoords = sprite.getTexCoords();
		int id = sprite.getTextureID();
		ImGui.pushID(i);
		if (ImGui.imageButton(
				id,
				spriteWidth,
				spriteHeight,
				texCoords[2].x,
				texCoords[0].y,
				texCoords[0].x,
				texCoords[2].y)) {
			GameObject ob = Prefabs.generateSpriteObject(sprite, CONSTANTS.GRID_WIDTH.getIntValue(), CONSTANTS.GRID_HEIGHT.getIntValue());
			if (!decoration) {
				RigidBody2D rb = new RigidBody2D();
				rb.setBodyType(BodyType.STATIC);
				ob.addComponent(rb);
				Box2DCollider collider = new Box2DCollider();
				collider.setHalfSize(new Vector2f(CONSTANTS.GRID_WIDTH.getIntValue(), CONSTANTS.GRID_HEIGHT.getIntValue()));
				ob.addComponent(collider);
				ob.addComponent(new Ground());
				if (i == 12 || i == 33) ob.addComponent(new BreakableBrick());
			}
			editor.getComponent(MouseControls.class).setActiveGameObject(ob);
		}
		ImGui.popID();
		ImVec2 lastButtonPos = ImGui.getItemRectMax();
		float lastButtonX = lastButtonPos.x;
		float nextButtonX = lastButtonX + itemSpacing.x + sprite.getWidth() * 3;
		if (i + 1 < blocks.getNumSprites() && nextButtonX < windowX + windowSize.x - 30) {
			ImGui.sameLine();
		}
	}

	@Override
	public void imGui() {
		ImGui.begin("Editor");
		Window.getScene().setDefaultScene(JImGui.inputText("Scene Name", Window.getScene().getDefaultScene()));
		editor.imGui();
		ImGui.end();

		ImGui.begin("World Blocks");
		ImVec2 windowPos = ImGui.getWindowPos(), windowSize = ImGui.getWindowSize(), itemSpacing = ImGui.getStyle().getItemSpacing();
		float windowX = windowPos.x + itemSpacing.x;
		if (ImGui.beginTabBar("Tabs")) {
			if (ImGui.beginTabItem("Solid Blocks")) {
				for (int i = 0; i < blocks.getNumSprites(); i++) {
					if (i == 34 || (i >= 38 && i < 61)) continue;
					addBlock(blocks, i, windowSize, itemSpacing, windowX, false);
				}
				ImGui.endTabItem();
			}
			if (ImGui.beginTabItem("Decoration Blocks")) {
				for (int i = 34; i < 61; i++) {
					if ((i >= 35 && i < 38) || (i >= 42 && i < 45)) continue;
					addBlock(blocks, i, windowSize, itemSpacing, windowX, true);
				}
				ImGui.endTabItem();
			}
			if (ImGui.beginTabItem("Prefabs")) {
				int uid = 0;
				SpriteSheet players = AssetPool.getSpriteSheet(CONSTANTS.SPRITE_SHEET_PATH.getValue(), 16, 16, 0, 26);
				Sprite sprite = players.getSprite(0);
				float spriteWidth = sprite.getWidth() * 3, spriteHeight = sprite.getHeight() * 3;
				int id = sprite.getTextureID();
				Vector2f[] texCoords = sprite.getTexCoords();
				ImGui.pushID(uid++);
				if (ImGui.imageButton(
						id,
						spriteWidth,
						spriteHeight,
						texCoords[2].x,
						texCoords[0].y,
						texCoords[0].x,
						texCoords[2].y)) {
					GameObject ob = Prefabs.generateMario();
					editor.getComponent(MouseControls.class).setActiveGameObject(ob);
				}
				ImGui.popID();
				ImGui.sameLine();
				SpriteSheet items = AssetPool.getSpriteSheet(CONSTANTS.ITEM_SHEET_PATH.getValue(), 16, 16, 0, 43);
				sprite = items.getSprite(0);
				id = sprite.getTextureID();
				texCoords = sprite.getTexCoords();
				ImGui.pushID(uid++);
				if (ImGui.imageButton(
						id,
						spriteWidth,
						spriteHeight,
						texCoords[2].x,
						texCoords[0].y,
						texCoords[0].x,
						texCoords[2].y)) {
					GameObject ob = Prefabs.generateQuestionBlock();
					editor.getComponent(MouseControls.class).setActiveGameObject(ob);
				}
				ImGui.popID();
				ImGui.sameLine();
				sprite = items.getSprite(7);
				id = sprite.getTextureID();
				texCoords = sprite.getTexCoords();
				ImGui.pushID(uid++);
				if (ImGui.imageButton(
						id,
						spriteWidth,
						spriteHeight,
						texCoords[2].x,
						texCoords[0].y,
						texCoords[0].x,
						texCoords[2].y)) {
					GameObject ob = Prefabs.generateCoin();
					editor.getComponent(MouseControls.class).setActiveGameObject(ob);
				}
				ImGui.popID();
				ImGui.sameLine();
				int[] goombaFrames = {14, 20};
				for (int i : goombaFrames) {
					sprite = players.getSprite(i);
					id = sprite.getTextureID();
					texCoords = sprite.getTexCoords();
					ImGui.pushID(uid++);
					if (ImGui.imageButton(
							id,
							spriteWidth,
							spriteHeight,
							texCoords[2].x,
							texCoords[0].y,
							texCoords[0].x,
							texCoords[2].y)) {
						GameObject ob = Prefabs.generateGoomba(i == 20);
						editor.getComponent(MouseControls.class).setActiveGameObject(ob);
					}
					ImGui.popID();
					ImGui.sameLine();
				}
				SpriteSheet pipes = AssetPool.getSpriteSheet(CONSTANTS.PIPES_SHEET_PATH.getValue(), 32, 32, 0, 4);
				PipeDirection[] directions = List.of(PipeDirection.values()).toArray(new PipeDirection[0]);
				int pipeIndex = 0;
				for (PipeDirection direction : directions) {
					sprite = pipes.getSprite(pipeIndex++);
					id = sprite.getTextureID();
					texCoords = sprite.getTexCoords();
					ImGui.pushID(uid++);
					if (ImGui.imageButton(
							id,
							spriteWidth,
							spriteHeight,
							texCoords[2].x,
							texCoords[0].y,
							texCoords[0].x,
							texCoords[2].y)) {
						GameObject ob = Prefabs.generatePipe(direction);
						editor.getComponent(MouseControls.class).setActiveGameObject(ob);
					}
					ImGui.popID();
					ImGui.sameLine();
				}
				SpriteSheet turtles = AssetPool.getSpriteSheet(CONSTANTS.TURTLE_SHEET_PATH.getValue(), 16, 24, 0, 4);
				sprite = turtles.getSprite(0);
				id = sprite.getTextureID();
				texCoords = sprite.getTexCoords();
				ImGui.pushID(uid++);
				if (ImGui.imageButton(
						id,
						spriteWidth,
						spriteHeight,
						texCoords[2].x,
						texCoords[0].y,
						texCoords[0].x,
						texCoords[2].y)) {
					GameObject ob = Prefabs.generateTurtle();
					editor.getComponent(MouseControls.class).setActiveGameObject(ob);
				}
				ImGui.popID();
				ImGui.sameLine();
				sprite = items.getSprite(6);
				id = sprite.getTextureID();
				texCoords = sprite.getTexCoords();
				ImGui.pushID(uid++);
				if (ImGui.imageButton(
						id,
						spriteWidth,
						spriteHeight,
						texCoords[2].x,
						texCoords[0].y,
						texCoords[0].x,
						texCoords[2].y)) {
					GameObject ob = Prefabs.generateFlag(true);
					editor.getComponent(MouseControls.class).setActiveGameObject(ob);
				}
				ImGui.popID();
				ImGui.sameLine();
				sprite = items.getSprite(33);
				id = sprite.getTextureID();
				texCoords = sprite.getTexCoords();
				ImGui.pushID(uid++);
				if (ImGui.imageButton(
						id,
						spriteWidth,
						spriteHeight,
						texCoords[2].x,
						texCoords[0].y,
						texCoords[0].x,
						texCoords[2].y)) {
					GameObject ob = Prefabs.generateFlag(false);
					editor.getComponent(MouseControls.class).setActiveGameObject(ob);
				}
				ImGui.popID();
				ImGui.sameLine();
				ImGui.endTabItem();
			}
			if (ImGui.beginTabItem("Sounds")) {
				List<Sound> sounds = loadSounds();
				for (Sound sound : sounds) {
					if (ImGui.button(new File(sound.getPath()).getName())) {
						if (sound.isPlaying()) {
							sound.stop();
						} else {
							sound.play();
						}
					}
					ImVec2 lastButtonPos = ImGui.getItemRectMax();
					if (lastButtonPos.x + itemSpacing.x + 100 < windowX + windowSize.x - 70) {
						ImGui.sameLine();
					}
				}
				ImGui.endTabItem();
			}
			ImGui.endTabBar();
		}
		ImGui.end();
	}
}
