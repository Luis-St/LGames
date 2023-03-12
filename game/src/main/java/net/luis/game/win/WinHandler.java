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
	
	boolean hasPlayerFinished(@NotNull GamePlayer player);
	
	boolean isDraw(@NotNull GameMap map);
	
	default boolean canPlayerWin(@NotNull GamePlayer player) {
		return true;
	}
	
	default @NotNull GameResultLine getResultLine(@NotNull GameMap map) {
		return GameResultLine.EMPTY;
	}
	
	void onPlayerFinished(@NotNull GamePlayer player);
	
	@NotNull List<GamePlayer> getFinishedPlayers();
	
	@NotNull List<GamePlayer> getWinOrder();
	
	int getScoreFor(@NotNull Game game, @NotNull GamePlayer player);
	
	void reset();
	
}
