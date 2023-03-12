package net.luis.ludo.player;

import com.google.common.collect.Lists;
import javafx.scene.image.ImageView;
import net.luis.fxutils.FxUtils;
import net.luis.game.application.FxApplication;
import net.luis.game.player.game.GamePlayerType;
import net.luis.language.TranslationKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public enum LudoPlayerType implements GamePlayerType {
	
	GREEN("green", new TranslationKey("screen.ludo.green_player"), "textures/ludo/figure/figure_green"), YELLOW("yellow", new TranslationKey("screen.ludo.yellow_player"), "textures/ludo/figure/figure_yellow"),
	BLUE("blue", new TranslationKey("screen.ludo.blue_player"), "textures/ludo/figure/figure_blue"), RED("red", new TranslationKey("screen.ludo.red_player"), "textures/ludo/figure/figure_red"),
	NO("no", new TranslationKey("screen.ludo.no_player"), null);
	
	private final String name;
	private final TranslationKey translation;
	private final String path;
	
	LudoPlayerType(String name, TranslationKey translation, String path) {
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
	public @NotNull List<LudoPlayerType> getOpponents() {
		return switch (this) {
			case GREEN -> Lists.newArrayList(YELLOW, BLUE, RED);
			case YELLOW -> Lists.newArrayList(GREEN, BLUE, RED);
			case BLUE -> Lists.newArrayList(GREEN, YELLOW, RED);
			case RED -> Lists.newArrayList(GREEN, YELLOW, BLUE);
			default -> Lists.newArrayList(NO);
		};
	}
	
	@Override
	public ImageView getImage(double width, double height) {
		if (this.path == null) {
			return null;
		}
		return FxUtils.makeImageView(Objects.requireNonNull(FxApplication.getInstance()).getResourceManager().resourceDirectory().resolve(this.path + ".png").toString(), width, height);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
