package net.vgc.game;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.game.action.ActionHandler;
import net.vgc.server.player.ServerPlayer;

public interface Game {
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	GameType<? extends Game> getType();
	
	ActionHandler getActionHandler();
	
	List<ServerPlayer> getPlayers();
	
	@Nullable
	ServerPlayer getCurrentPlayer();
	
	void setCurrentPlayer(ServerPlayer currentPlayer);
	
	default void nextPlayer() {
		List<ServerPlayer> players = this.getPlayers();
		if (!players.isEmpty()) {
			if (this.getCurrentPlayer() == null) {
				this.setCurrentPlayer(players.get(0));
			} else {
				int index = players.indexOf(this.getCurrentPlayer());
				if (index != -1) {
					index++;
					if (index >= players.size()) {
						this.setCurrentPlayer(players.get(0));
					} else {
						this.setCurrentPlayer(players.get(index));
					}
				} else {
					this.setCurrentPlayer(players.get(0));
				}
			}
		} else {
			LOGGER.warn("Unable to change player, since there is no player present");
		}
	}
	
	boolean removePlayer(ServerPlayer player);
	
	void stopGame();
	
}
