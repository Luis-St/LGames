package net.luis.game.map;

import net.luis.game.Game;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

@FunctionalInterface
public interface GameMapFactory {
	
	@NotNull GameMap create(@NotNull Game game);
}
