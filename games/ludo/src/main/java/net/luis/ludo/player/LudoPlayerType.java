package net.luis.ludo.player;

import com.google.common.collect.Lists;
import javafx.scene.image.ImageView;
import net.luis.fxutils.FxUtils;
import net.luis.game.application.GameApplication;
import net.luis.game.player.GamePlayerType;
import net.luis.language.TranslationKey;

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
	public TranslationKey getTranslation() {
		return this.translation;
	}
	
	@Override
	public List<LudoPlayerType> getOpponents() {
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
		return FxUtils.makeImageView(Objects.requireNonNull(GameApplication.getInstance()).getResourceDirectory().resolve(this.path + ".png").toString(), width, height);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
