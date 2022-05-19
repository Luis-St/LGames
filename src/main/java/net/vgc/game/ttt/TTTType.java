package net.vgc.game.ttt;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import javafx.scene.image.ImageView;
import net.vgc.client.fx.FxUtil;
import net.vgc.game.GamePlayerType;

public enum TTTType implements GamePlayerType {
	
	CROSS("cross", 0, 'X', "textures/tic_tac_toe/cross/cross"),
	CIRCLE("circle", 1, 'O', "textures/tic_tac_toe/circle/circle"),
	NO("no", 2, 'N', null);
	
	private final String name;
	private final int id;
	private final char character;
	private final String path;
	
	private TTTType(String name, int id, char character, String path) {
		this.name = name;
		this.id = id;
		this.character = character;
		this.path = path;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public int getId() {
		return this.id;
	}
	
	public char getCharacter() {
		return this.character;
	}
	
	@Nullable
	public ImageView getImage(TTTState state, double width, double height) {
		if (this.path == null) {
			return null;
		}
		return FxUtil.makeImageView(this.path + (state.getState().isEmpty() ? "" : "_") + state.getState() + ".png", width, height);
	}
	
	@Override
	public List<TTTType> getOpponents() {
		if (this == NO) {
			return Lists.newArrayList(NO);
		}
		return this == CROSS ? Lists.newArrayList(CIRCLE) : Lists.newArrayList(CROSS);
	}
	
	@Override
	public Enum<TTTType> getDefault() {
		return NO;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
