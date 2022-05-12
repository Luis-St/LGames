package net.vgc.game.ttt;

import javax.annotation.Nullable;

import javafx.scene.image.ImageView;
import net.vgc.Main;
import net.vgc.client.fx.FxUtil;

public enum TTTType {
	
	CROSS("cross", 'X', 0, "textures/cross"),
	CIRCLE("circle", 'O', 1, "textures/circle"),
	NO("no", 'N', -1, null);
	
	private final String name;
	private final char character;
	private final int id;
	private final String path;
	
	private TTTType(String name, char character, int id, String path) {
		this.name = name;
		this.character = character;
		this.id = id;
		this.path = path;
	}
	
	public String getName() {
		return this.name;
	}
	
	public char getCharacter() {
		return this.character;
	}
	
	public int getId() {
		return this.id;
	}
	
	@Nullable
	public ImageView getImage(TTTState state, double width, double height) {
		return FxUtil.makeImageView(this.path + (state.getState().isEmpty() ? "" : "_") + state.getState() + ".png", width, height);
	}
	
	public TTTType getOpponent() {
		if (this == NO) {
			return NO;
		}
		return this == CROSS ? CIRCLE : CROSS;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public static TTTType fromId(int id) {
		for (TTTType type : values()) {
			if (type.getId() == id) {
				return type;
			}
		}
		Main.LOGGER.warn("Fail to get tic tac toe type from id {}", id);
		return NO;
	}
	
}
