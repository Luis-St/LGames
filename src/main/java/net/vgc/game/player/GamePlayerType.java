package net.vgc.game.player;

import java.util.List;

import net.vgc.language.TranslationKey;
import net.vgc.util.EnumRepresentable;

public interface GamePlayerType extends EnumRepresentable {
	
	TranslationKey getTranslation();
	
	List<? extends GamePlayerType> getOpponents();
	
}
