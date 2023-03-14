package net.luis.wins4.player;

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

public enum Wins4PlayerType implements GamePlayerType {
	
	YELLOW("yellow",new TranslationKey("screen.win4.yellow_player"), "textures/wins4/figure/figure_yellow"), RED("red", new TranslationKey("screen.win4.red_player"), "textures/wins4/figure/figure_red"),
	NO("no", new TranslationKey("screen.tic_tac_toe.no_player"), null);
	
	private final String name;
	private final TranslationKey translation;
	private final String path;
	
	Wins4PlayerType(String name, TranslationKey translation, String path) {
		this.name = name;
		this.translation = translation;
		this.path = path;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public @NotNull TranslationKey getTranslation() {
		return this.translation;
	}
	
	@Override
	public @NotNull List<Wins4PlayerType> getOpponents() {
		if (this == NO) {
			return Lists.newArrayList(NO);
		}
		return this == YELLOW ? Lists.newArrayList(RED) : Lists.newArrayList(YELLOW);
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
