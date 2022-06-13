package net.vgc.server.game.win;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.server.game.map.ServerGameMap;
import net.vgc.server.game.player.ServerGamePlayer;

public interface WinHandler {
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	boolean hasMultipleWinners();
	
	boolean hasWinner();
	
	ServerGamePlayer getWinningPlayer();
	
	boolean hasWinners();
	
	List<? extends ServerGamePlayer> getWinningPlayers();
	
	boolean isPlayerFinished(ServerGamePlayer player);
	
	boolean isDraw(ServerGameMap map);
	
	default boolean canPlayerWin(ServerGamePlayer player) {
		return true;
	}
	
	void onPlayerFinished(ServerGamePlayer player);
	
	List<? extends ServerGamePlayer> getFinishedPlayers();
	
	List<? extends ServerGamePlayer> getWinOrder();
	
}
