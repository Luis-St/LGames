package net.vgc.games.ludo.player;

import com.google.common.collect.Lists;
import javafx.scene.image.ImageView;
import net.luis.fxutils.FxUtils;
import net.vgc.game.player.GamePlayerType;
import net.vgc.language.TranslationKey;
import net.vgc.network.Network;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public enum LudoPlayerType implements GamePlayerType {
	
	GREEN("green", 0, new TranslationKey("screen.ludo.green_player"), "textures/ludo/figure/figure_green"), YELLOW("yellow", 1, new TranslationKey("screen.ludo.yellow_player"), "textures/ludo/figure/figure_yellow"),
	BLUE("blue", 2, new TranslationKey("screen.ludo.blue_player"), "textures/ludo/figure/figure_blue"), RED("red", 3, new TranslationKey("screen.ludo.red_player"), "textures/ludo/figure/figure_red"),
	NO("no", 4, new TranslationKey("screen.ludo.no_player"), null);
	
	private final String name;
	private final int id;
	private final TranslationKey translation;
	private final String path;
	
	LudoPlayerType(String name, int id, TranslationKey translation, String path) {
		this.name = name;
		this.id = id;
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
	public Enum<LudoPlayerType> getDefault() {
		return NO;
	}
	
	@Override
	public ImageView getImage(double width, double height) {
		if (this.path == null) {
			return null;
		}
		return FxUtils.makeImageView(Network.INSTANCE.getResourceDirectory().resolve(this.path + ".png").toString(), width, height);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
