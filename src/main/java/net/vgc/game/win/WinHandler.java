package net.vgc.game.win;

import net.vgc.game.Game;
import net.vgc.game.map.GameMap;
import net.vgc.game.player.GamePlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public interface WinHandler {
	
	Logger LOGGER = LogManager.getLogger();
	
	boolean hasMultipleWinners();
	
	boolean hasWinner();
	
	GamePlayer getWinningPlayer();
	
	boolean hasWinners();
	
	List<GamePlayer> getWinningPlayers();
	
	boolean hasPlayerFinished(GamePlayer player);
	
	boolean isDraw(GameMap map);
	
	default boolean canPlayerWin(GamePlayer player) {
		return true;
	}
	
	default GameResultLine getResultLine(GameMap map) {
		return GameResultLine.EMPTY;
	}
	
	void onPlayerFinished(GamePlayer player);
	
	List<GamePlayer> getFinishedPlayers();
	
	List<GamePlayer> getWinOrder();
	
	int getScoreFor(Game game, GamePlayer player);
	
	void reset();
	
}
