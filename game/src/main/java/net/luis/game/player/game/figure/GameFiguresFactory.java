package net.luis.game.player.game.figure;

import net.luis.game.player.game.GamePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

@FunctionalInterface
public interface GameFiguresFactory {
	
	@NotNull List<GameFigure> create(@NotNull GamePlayer player);
}
