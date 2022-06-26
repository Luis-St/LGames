package net.vgc.game.games.ttt.player;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import javafx.scene.image.ImageView;
import net.vgc.client.fx.FxUtil;
import net.vgc.client.game.games.ttt.map.field.TTTFieldRenderState;
import net.vgc.game.player.GamePlayerType;
import net.vgc.language.TranslationKey;

public enum TTTPlayerType implements GamePlayerType {
	
	CROSS("cross", 0, 'X', new TranslationKey("screen.tic_tac_toe.cross_player"), "textures/tic_tac_toe/cross/cross"),
	CIRCLE("circle", 1, 'O', new TranslationKey("screen.tic_tac_toe.circle_player"), "textures/tic_tac_toe/circle/circle"),
	NO("no", 2, 'N', new TranslationKey("screen.tic_tac_toe.no_player"), null);
	
	private final String name;
	private final int id;
	private final char character;
	private final TranslationKey translation;
	private final String path;
	
	private TTTPlayerType(String name, int id, char character, TranslationKey translation, String path) {
		this.name = name;
		this.id = id;
		this.character = character;
		this.translation = translation;
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
	
	@Override
	public TranslationKey getTranslation() {
		return this.translation;
	}

	@Override
	public List<TTTPlayerType> getOpponents() {
		if (this == NO) {
			return Lists.newArrayList(NO);
		}
		return this == CROSS ? Lists.newArrayList(CIRCLE) : Lists.newArrayList(CROSS);
	}
	
	@Override
	public Enum<TTTPlayerType> getDefault() {
		return NO;
	}
	
	@Nullable
	public ImageView getImage(TTTFieldRenderState renderState, double width, double height) {
		if (this.path == null) {
			return null;
		} else if (renderState == TTTFieldRenderState.NO) {
			return null;
		}
		return FxUtil.makeImageView(this.path + (renderState.getState().isEmpty() ? "" : "_") + renderState.getState() + ".png", width, height);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
