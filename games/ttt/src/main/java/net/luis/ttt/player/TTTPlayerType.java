package net.luis.ttt.player;

import com.google.common.collect.Lists;
import javafx.scene.image.ImageView;
import net.luis.fxutils.FxUtils;
import net.luis.game.application.FxApplication;
import net.luis.game.player.game.GamePlayerType;
import net.luis.language.TranslationKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

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
	public @NotNull TranslationKey getTranslation() {
		return this.translation;
	}
	
	@Override
	public @NotNull List<TTTPlayerType> getOpponents() {
		if (this == NO) {
			return Lists.newArrayList(NO);
		}
		return this == CROSS ? Lists.newArrayList(CIRCLE) : Lists.newArrayList(CROSS);
	}
	
	@Override
	public @Nullable ImageView getImage(@NotNull String suffix, double width, double height) {
		if (this.path == null) {
			return null;
		}
		return FxUtils.makeImageView(Objects.requireNonNull(FxApplication.getInstance()).getResourceManager().resourceDirectory().resolve(this.path + suffix + ".png").toString(), width, height);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
