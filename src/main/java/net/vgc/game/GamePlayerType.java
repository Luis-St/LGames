package net.vgc.game;

import java.util.List;

import net.vgc.util.EnumRepresentable;

public interface GamePlayerType extends EnumRepresentable {

	List<? extends GamePlayerType> getOpponents();

}
