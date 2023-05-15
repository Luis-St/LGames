package net.luis.game.screen;

import net.luis.game.Game;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

@FunctionalInterface
public interface GameScreenFactory {
	
	@NotNull GameScreen create(@NotNull Game game);
}
