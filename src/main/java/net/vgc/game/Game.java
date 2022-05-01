package net.vgc.game;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.game.action.ActionHandler;
import net.vgc.network.packet.client.ExitGamePacket;
import net.vgc.network.packet.client.StopGamePacket;
import net.vgc.server.Server;
import net.vgc.server.dedicated.DedicatedServer;
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
	
	default boolean removePlayer(ServerPlayer player) {
		if (this.getPlayers().remove(player)) {
			player.connection.send(new ExitGamePacket());
			if (Objects.equals(this.getCurrentPlayer(), player)) {
				this.nextPlayer();
			}
			if (!this.getType().enoughPlayersToPlay(this.getPlayers())) {
				this.stopGame();
			}
			return true;
		}
		return false;
	}
	
	default void stopGame() {
		DedicatedServer server = Server.getInstance().getServer();
		for (ServerPlayer player : this.getPlayers()) {
			player.connection.send(new StopGamePacket());
		}
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (this.getPlayers().contains(player) && player.isPlaying()) {
				player.setPlaying(false);
			} else if (player.isPlaying()) {
				player.setPlaying(false);
				LOGGER.info("Correcting the playing value of player {} to false, since it was not correctly reset", player.getGameProfile().getName());
			}
		}
		this.getPlayers().clear();
		server.setGame(null);
		LOGGER.info("Game {} was successfully stopped", this.getType().getName());
	}
	
}
