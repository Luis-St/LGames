package net.luis.game;

import net.luis.game.application.GameApplication;
import net.luis.game.player.GamePlayerInfo;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

@FunctionalInterface
public interface GameFactory<T extends Game> {
	
	T createGame(GameApplication application, List<GamePlayerInfo> playerInfos);
	
}
