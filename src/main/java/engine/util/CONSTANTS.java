package engine.util;

public enum CONSTANTS {
	DEBUG(0),
	GRID_WIDTH(0.25f),
	GRID_HEIGHT(0.25f),
	IMGUI_PATH("bin/imgui/"),
	RESOURCE_PATH("bin/savefiles/"),
	LOGO_PATH("assets/images/logo.png"),
	GIZMOS_PATH("assets/images/gizmos.png"),
	GAME_FONT_PATH("assets/textures/fonts/mario.ttf"),
	FONT_PATH("assets/textures/fonts/segoeui.ttf"),
	SOUNDS_PATH("assets/sounds/"),
	TURTLE_SHEET_PATH("assets/textures/sprites/turtle.png"),
	PIPES_SHEET_PATH("assets/textures/sprites/pipes.png"),
	POWER_SHEET_PATH("assets/textures/sprites/power_sprites.png"),
	ITEM_SHEET_PATH("assets/textures/sprites/items.png"),
	BLOCK_SHEET_PATH("assets/textures/sprites/blocks.png"),
	SPRITE_SHEET_PATH("assets/textures/sprites/spritesheet.png"),
	FONT_SHADER_PATH("assets/shaders/font.glsl"),
	PICKER_SHADER_PATH("assets/shaders/picker.glsl"),
	DEFAULT_SHADER_PATH("assets/shaders/default.glsl"),
	LINE2D_SHADER_PATH("assets/shaders/line2d.glsl");

	private final String value;
	private final float intValue;

	CONSTANTS(float intValue) {
		this.intValue = intValue;
		this.value = String.valueOf(intValue);
	}

	CONSTANTS(String value) {
		this.value = value;
		this.intValue = 0;
	}

	public String getValue() {
		return value;
	}

	public float getIntValue() {
		return intValue;
	}
}
