package net.luis.game.win;

import net.luis.game.Game;
import net.luis.game.map.GameMap;
import net.luis.game.player.game.GamePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public interface WinHandler {
	
	boolean hasMultipleWinners();
	
	boolean hasWinner();
	
	@NotNull GamePlayer getWinningPlayer();
	
	boolean hasWinners();
	
	@NotNull List<GamePlayer> getWinningPlayers();
	
	boolean hasPlayerFinished(GamePlayer player);
	
	boolean isDraw(GameMap map);
	
	default boolean canPlayerWin(GamePlayer player) {
		return true;
	}
	
	default @NotNull GameResultLine getResultLine(GameMap map) {
		return GameResultLine.EMPTY;
	}
	
	void onPlayerFinished(GamePlayer player);
	
	@NotNull List<GamePlayer> getFinishedPlayers();
	
	@NotNull List<GamePlayer> getWinOrder();
	
	int getScoreFor(Game game, GamePlayer player);
	
	void reset();
}
