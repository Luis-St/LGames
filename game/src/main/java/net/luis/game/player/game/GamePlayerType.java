package net.luis.game.player.game;

import javafx.scene.image.ImageView;
import net.luis.language.TranslationKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public interface GamePlayerType {
	
	@NotNull TranslationKey getTranslation();
	
	@NotNull List<? extends GamePlayerType> getOpponents();
	
	default @Nullable ImageView getImage(double width, double height) {
		return this.getImage("", width, height);
	}
	
	ImageView getImage(String suffix, double width, double height);
}
