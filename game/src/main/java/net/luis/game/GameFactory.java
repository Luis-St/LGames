package net.luis.game;

import net.luis.game.application.FxApplication;
import net.luis.game.player.game.GamePlayerInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

@FunctionalInterface
public interface GameFactory<T extends Game> {
	
	@NotNull T createGame(@NotNull FxApplication application, @NotNull List<GamePlayerInfo> playerInfos);
	
}
