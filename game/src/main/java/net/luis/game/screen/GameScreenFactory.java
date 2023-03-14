package net.luis.game.screen;

import net.luis.game.Game;
import net.luis.game.screen.GameScreen;
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
