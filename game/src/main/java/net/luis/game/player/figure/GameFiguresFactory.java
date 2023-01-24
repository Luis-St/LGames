package net.luis.game.player.figure;

import net.luis.game.player.GamePlayer;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

@FunctionalInterface
public interface GameFiguresFactory {
	
	List<GameFigure> create(GamePlayer player);
	
}
