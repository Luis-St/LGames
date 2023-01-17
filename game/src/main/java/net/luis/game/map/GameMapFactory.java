package net.luis.game.map;

import net.luis.game.Game;

/**
 *
 * @author Luis-st
 *
 */

@FunctionalInterface
public interface GameMapFactory {
	
	GameMap create(Game game);
	
}
