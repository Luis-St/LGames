package net.luis.ttt.player;

import com.google.common.collect.Lists;
import javafx.scene.image.ImageView;
import net.luis.language.TranslationKey;
import net.luis.game.player.GamePlayerType;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public enum TTTPlayerType implements GamePlayerType {
	
	CROSS("cross", 'X', new TranslationKey("screen.tic_tac_toe.cross_player"), "textures/tic_tac_toe/cross/cross"), CIRCLE("circle", 'O', new TranslationKey("screen.tic_tac_toe.circle_player"), "textures/tic_tac_toe/circle/circle"),
	NO("no", 'N', new TranslationKey("screen.tic_tac_toe.no_player"), null);
	
	private final String name;
	private final char character;
	private final TranslationKey translation;
	private final String path;
	
	TTTPlayerType(String name, char character, TranslationKey translation, String path) {
		this.name = name;
		this.character = character;
		this.translation = translation;
		this.path = path;
	}
	
	public String getName() {
		return this.name;
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
	
	public String getPath() {
		return this.path;
	}
	
	@Override
	public ImageView getImage(double width, double height) {
		return null;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
