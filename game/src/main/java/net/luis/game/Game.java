package net.luis.game;

import com.google.common.collect.Lists;
import net.luis.application.ApplicationType;
import net.luis.client.Client;
import net.luis.client.player.AbstractClientPlayer;
import net.luis.client.screen.LobbyScreen;
import net.luis.game.dice.DiceHandler;
import net.luis.game.map.GameMap;
import net.luis.game.map.field.GameField;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.GamePlayerType;
import net.luis.game.type.GameType;
import net.luis.game.win.WinHandler;
import net.luis.network.packet.Packet;
import net.luis.network.packet.client.SyncPlayerDataPacket;
import net.luis.network.packet.client.game.ExitGamePacket;
import net.luis.network.packet.client.game.StopGamePacket;
import net.luis.network.packet.client.game.UpdateGameMapPacket;
import net.luis.player.GameProfile;
import net.luis.player.Player;
import net.luis.server.Server;
import net.luis.server.player.ServerPlayer;
import net.luis.utils.math.Mth;
import net.luis.utils.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 *
 * @author Luis-st
 *
 */

public interface Game {
	
	Logger LOGGER = LogManager.getLogger();
	
	default void init() {
		
	}
	
	default void start() {
		
	}
	
	GameType<? extends Game, ? extends Game> getType();
	
	GameMap getMap();
	
	List<GamePlayer> getPlayers();
	
	default List<GamePlayer> getEnemies(GamePlayer gamePlayer) {
		List<GamePlayer> enemies = Lists.newArrayList();
		for (GamePlayer player : this.getPlayers()) {
			if (!player.equals(gamePlayer)) {
				enemies.add(player);
			}
		}
		if (enemies.isEmpty()) {
			LOGGER.warn("Fail to get enemies for player {}", gamePlayer.getName());
		}
		return enemies;
	}
	
	@Nullable
	default GamePlayer getPlayerFor(Player player) {
		for (GamePlayer gamePlayer : this.getPlayers()) {
			if (gamePlayer.getPlayer().equals(player)) {
				return gamePlayer;
			}
		}
		return null;
	}
	
	@Nullable
	default GamePlayer getPlayerFor(GameProfile profile) {
		for (GamePlayer gamePlayer : this.getPlayers()) {
			if (gamePlayer.getPlayer().getProfile().equals(profile)) {
				return gamePlayer;
			}
		}
		return null;
	}
	
	@Nullable
	default GamePlayerType getPlayerType(GamePlayer player) {
		for (GamePlayer gamePlayer : this.getPlayers()) {
			if (gamePlayer.equals(player)) {
				return gamePlayer.getPlayerType();
			}
		}
		return null;
	}
	
	@Nullable
	GamePlayer getPlayer();
	
	void setPlayer(GamePlayer player);
	
	default GamePlayer getStartPlayer() {
		this.nextPlayer(true);
		return ApplicationType.SERVER.isOn() ? this.getPlayer() : null;
	}
	
	default void nextPlayer(boolean random) {
		ApplicationType.SERVER.executeIfOn(() -> {
			List<? extends GamePlayer> players = this.getPlayers();
			if (!players.isEmpty()) {
				if (random) {
					this.setPlayer(players.get(new Random().nextInt(players.size())));
				} else {
					GamePlayer player = this.getPlayer();
					if (player == null) {
						this.setPlayer(players.get(0));
					} else {
						int index = players.indexOf(player);
						if (index != -1) {
							index++;
							if (index >= players.size()) {
								this.setPlayer(players.get(0));
							} else {
								this.setPlayer(players.get(index));
							}
						} else {
							LOGGER.warn("Fail to get next player, since the player {} does not exists", player.getName());
							this.setPlayer(players.get(0));
						}
					}
				}
			} else {
				LOGGER.warn("Unable to change player, since there is no player present");
			}
		});
	}
	
	default boolean removePlayer(GamePlayer gamePlayer, boolean sendExit) {
		if (ApplicationType.SERVER.isOn()) {
			if (this.getPlayers().remove(gamePlayer)) {
				if (gamePlayer.getPlayer() instanceof ServerPlayer player) {
					if (sendExit) {
						player.connection.send(new ExitGamePacket());
					}
					player.setPlaying(false);
					Game.LOGGER.info("Remove player {} from game {}", player.getName(), this.getType().getName().toLowerCase());
					if (Objects.equals(this.getPlayer(), gamePlayer)) {
						this.nextPlayer(false);
					}
					if (!Mth.isInBounds(this.getPlayers().size(), this.getType().getMinPlayers(), this.getType().getMaxPlayers())) {
						this.stop();
					}
					player.getScore().reset();
					this.broadcastPlayersExclude(new SyncPlayerDataPacket(player), gamePlayer);
					return true;
				} else {
					Game.LOGGER.warn("Fail to remove player {}, since the player is not a server player", gamePlayer.getName());
				}
			} else if (gamePlayer != null) {
				Game.LOGGER.warn("Fail to remove player {}, since the player does not playing game {}", gamePlayer.getName(), this.getType().getInfoName());
				if (gamePlayer.getPlayer().isPlaying()) {
					gamePlayer.getPlayer().setPlaying(false);
					Game.LOGGER.info("Correcting the playing value of player {} to false, since it was not correctly reset", gamePlayer.getName());
				}
			}
		}
		return false;
	}
	
	default boolean isDiceGame() {
		return false;
	}
	
	@Nullable
	default DiceHandler getDiceHandler() {
		return null;
	}
	
	@Nullable
	default WinHandler getWinHandler() {
		return null;
	}
	
	default boolean nextMatch() {
		if (Mth.isInBounds(this.getPlayers().size(), this.getType().getMinPlayers(), this.getType().getMaxPlayers())) {
			this.getMap().reset();
			this.getMap().init(this.getPlayers());
			if (this.isDiceGame()) {
				Objects.requireNonNull(this.getDiceHandler()).reset();
			}
			Objects.requireNonNull(this.getWinHandler()).reset();
			this.nextPlayer(true);
			this.broadcastPlayers(new UpdateGameMapPacket(Utils.mapList(this.getMap().getFields(), GameField::getFieldInfo)));
			LOGGER.info("Start a new match of game {} with players {}", this.getType().getInfoName(), Utils.mapList(this.getPlayers(), GamePlayer::getName));
			return true;
		}
		LOGGER.warn("Fail to start a new match of game {}, since the player count {} is not in bound {} - {} ", this.getType().getName().toLowerCase(), this.getPlayers().size(), this.getType().getMinPlayers(), this.getType().getMaxPlayers());
		return false;
	}
	
	default void stop() {
		Game.LOGGER.info("Stopping the current game {}", this.getType().getInfoName());
		ApplicationType.SERVER.executeIfOn(() -> {
			for (ServerPlayer player : Server.getInstance().getPlayerList().getPlayers()) {
				if (player.isPlaying()) {
					if (Utils.mapList(this.getPlayers(), GamePlayer::getPlayer).contains(player)) {
						player.setPlaying(false);
					} else {
						player.setPlaying(false);
						Game.LOGGER.info("Correcting the playing value of player {} to false, since it was not correctly reset", player.getName());
					}
				}
			}
			for (GamePlayer gamePlayer : this.getPlayers()) {
				this.broadcastPlayer(new StopGamePacket(), gamePlayer);
				gamePlayer.getPlayer().getScore().reset();
				this.broadcastPlayersExclude(new SyncPlayerDataPacket(gamePlayer), gamePlayer);
			}
			this.getPlayers().clear();
			Server.getInstance().setGame(null);
		});
		ApplicationType.CLIENT.executeIfOn(() -> {
			for (AbstractClientPlayer player : Client.getInstance().getPlayers()) {
				player.setPlaying(false);
				player.getScore().reset();
			}
			Client.getInstance().setScreen(new LobbyScreen());
		});
		Game.LOGGER.info("Game {} was successfully stopped", this.getType().getInfoName());
	}
	
	default void broadcastPlayer(Packet packet, GamePlayer gamePlayer) {
		if (gamePlayer.getPlayer() instanceof ServerPlayer player) {
			player.connection.send(packet);
		}
	}
	
	default void broadcastPlayers(Packet packet) {
		for (GamePlayer player : this.getPlayers()) {
			this.broadcastPlayer(packet, player);
		}
	}
	
	default void broadcastPlayersExclude(Packet packet, GamePlayer... gamePlayers) {
		for (GamePlayer player : this.getPlayers()) {
			if (!Lists.newArrayList(gamePlayers).contains(player)) {
				this.broadcastPlayer(packet, player);
			}
		}
	}
	
}
