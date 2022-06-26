package net.vgc.game.games.ludo.player;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import javafx.scene.image.ImageView;
import net.vgc.client.fx.FxUtil;
import net.vgc.game.player.GamePlayerType;
import net.vgc.language.TranslationKey;

public enum LudoPlayerType implements GamePlayerType {
	
	GREEN("green", 0, new TranslationKey("screen.ludo.green_player"), "textures/ludo/figure/figure_green"),
	YELLOW("yellow", 1, new TranslationKey("screen.ludo.yellow_player"), "textures/ludo/figure/figure_yellow"),
	BLUE("blue", 2, new TranslationKey("screen.ludo.blue_player"), "textures/ludo/figure/figure_blue"),
	RED("red", 3, new TranslationKey("screen.ludo.red_player"), "textures/ludo/figure/figure_red"),
	NO("no", 4, new TranslationKey("screen.ludo.no_player"), null);
	
	private final String name;
	private final int id;
	private final TranslationKey translation;
	private final String path;
	
	private LudoPlayerType(String name, int id, TranslationKey translation, String path) {
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
		switch (this) {
			case GREEN: return Lists.newArrayList(YELLOW, BLUE, RED);
			case YELLOW: return Lists.newArrayList(GREEN, BLUE, RED);
			case BLUE: return Lists.newArrayList(GREEN, YELLOW, RED);
			case RED: return Lists.newArrayList(GREEN, YELLOW, BLUE);
			default: break;
		}
		return Lists.newArrayList(NO);
	}
	
	@Override
	public Enum<LudoPlayerType> getDefault() {
		return NO;
	}
	
	@Nullable
	public ImageView getImage( double width, double height) {
		if (this.path == null) {
			return null;
		}
		return FxUtil.makeImageView(this.path + ".png", width, height);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
