package net.luis.game.player.game;

import net.luis.game.Game;
import net.luis.game.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

@FunctionalInterface
public interface GamePlayerFactory {
	
	@NotNull GamePlayer create(@NotNull Game game, @NotNull Player player, @NotNull GamePlayerType playerType, @NotNull List<UUID> uniqueIds);
}
