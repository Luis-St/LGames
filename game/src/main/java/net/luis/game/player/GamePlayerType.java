package net.luis.game.player;

import javafx.scene.image.ImageView;
import net.luis.common.util.EnumRepresentable;
import net.luis.language.TranslationKey;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public interface GamePlayerType extends EnumRepresentable {
	
	TranslationKey getTranslation();
	
	List<? extends GamePlayerType> getOpponents();
	
	@Nullable
	ImageView getImage(double width, double height);
	
}
