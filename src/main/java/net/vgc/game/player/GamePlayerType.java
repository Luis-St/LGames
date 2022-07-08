package net.vgc.game.player;

import java.util.List;

import javax.annotation.Nullable;

import javafx.scene.image.ImageView;
import net.vgc.language.TranslationKey;
import net.vgc.util.EnumRepresentable;

public interface GamePlayerType extends EnumRepresentable {
	
	TranslationKey getTranslation();
	
	List<? extends GamePlayerType> getOpponents();
	
	@Nullable
	ImageView getImage(double width, double height);
	
}
