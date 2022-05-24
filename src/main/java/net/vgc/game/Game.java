package net.vgc.game;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import net.vgc.game.dice.DiceHandler;
import net.vgc.game.score.GameScore;
import net.vgc.network.packet.Packet;
import net.vgc.network.packet.client.SyncPlayerDataPacket;
import net.vgc.network.packet.client.game.ExitGamePacket;
import net.vgc.network.packet.client.game.StopGamePacket;
import net.vgc.server.Server;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Util;

public interface Game {
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	default DedicatedServer getServer() {
		return Server.getInstance().getServer();
	}
	
	default void broadcastPlayers(Packet<?> packet) {
		this.getServer().getPlayerList().broadcastAll(this.getPlayers(), packet);
	}
	
	GameType<? extends Game> getType();
	
	List<ServerPlayer> getPlayers();
	
	GameScore getScore();
	
	@Nullable
	ServerPlayer getCurrentPlayer();
	
	void setCurrentPlayer(ServerPlayer currentPlayer);
	
	default ServerPlayer getStartPlayer() {
		this.randomNextPlayer();
		return this.getCurrentPlayer();
	}
	
	default List<ServerPlayer> getEnemiesFor(ServerPlayer player) {
		List<ServerPlayer> enemies = Lists.newArrayList();
		for (ServerPlayer serverPlayer : this.getPlayers()) {
			if (!serverPlayer.equals(player)) {
				enemies.add(serverPlayer);
			}
		}
		if (enemies.isEmpty()) {
			LOGGER.warn("Fail to get enemies for player {}", player.getProfile().getName());
		}
		return enemies;
	}
	
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
	
	default void randomNextPlayer() {
		List<ServerPlayer> players = this.getPlayers();
		if (!players.isEmpty()) {
			Collections.shuffle(players, Util.systemRandom());
			this.setCurrentPlayer(players.get(0));
		} else {
			LOGGER.warn("Unable to change player, since there is no player present");
		}
	}
	
	default boolean removePlayer(ServerPlayer player, boolean sendExit) {
		if (this.getPlayers().remove(player)) {
			if (sendExit) {
				player.connection.send(new ExitGamePacket());
			}
			player.setPlaying(false);
			LOGGER.info("Remove player {} from game {}", player.getProfile().getName(), this.getType().getName().toLowerCase());
			if (Objects.equals(this.getCurrentPlayer(), player)) {
				this.nextPlayer();
			}
			if (!this.getType().enoughPlayersToPlay(this.getPlayers())) {
				this.stopGame();
			}
			this.getServer().getPlayerList().broadcastAllExclude(new SyncPlayerDataPacket(player.getProfile(), player.isPlaying()), player);
			return true;
		}
		if (player != null) {
			LOGGER.warn("Fail to remove player {}, since the player does not playing game {}", player.getProfile().getName(), this.getType().getName().toLowerCase());
			if (player.isPlaying()) {
				player.setPlaying(false);
				LOGGER.info("Correcting the playing value of player {} to false, since it was not correctly reset", player.getProfile().getName());
			}
		}
		return false;
	}
	
	@Nullable
	GamePlayerType getPlayerType(ServerPlayer player);
	
	boolean nextMatch();
	
	default boolean isDiceGame() {
		return false;
	}
	
	@Nullable
	default DiceHandler getDiceHandler() {
		return null;
	}
	
	default void stopGame() {
		this.onStop();
		for (ServerPlayer player : this.getServer().getPlayerList().getPlayers()) {
			if (this.getPlayers().contains(player) && player.isPlaying()) {
				player.setPlaying(false);
			} else if (player.isPlaying()) {
				player.setPlaying(false);
				LOGGER.info("Correcting the playing value of player {} to false, since it was not correctly reset", player.getProfile().getName());
			}
		}
		for (ServerPlayer player : this.getPlayers()) {
			player.connection.send(new StopGamePacket());
			this.getServer().getPlayerList().broadcastAllExclude(new SyncPlayerDataPacket(player.getProfile(), player.isPlaying()), player);
		}
		this.getPlayers().clear();
		this.getServer().setGame(null);
		LOGGER.info("Game {} was successfully stopped", this.getType().getName().toLowerCase());
	}
	
	default void onStart() {
		
	}
	
	default void onStarted() {
		
	}
	
	default void onStop() {
		
	}
	
}
