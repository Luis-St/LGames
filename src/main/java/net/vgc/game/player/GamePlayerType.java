package net.vgc.game.player;

import javafx.scene.image.ImageView;
import net.vgc.language.TranslationKey;
import net.vgc.util.EnumRepresentable;
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
