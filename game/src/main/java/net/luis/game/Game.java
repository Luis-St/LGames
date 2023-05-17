package net.luis.game;

import com.google.common.collect.Lists;
import net.luis.game.application.ApplicationType;
import net.luis.game.application.FxApplication;
import net.luis.game.application.GameApplication;
import net.luis.game.dice.DiceHandler;
import net.luis.game.map.GameMap;
import net.luis.game.map.field.GameField;
import net.luis.game.player.GameProfile;
import net.luis.game.player.Player;
import net.luis.game.player.game.GamePlayer;
import net.luis.game.player.game.GamePlayerType;
import net.luis.game.screen.GameScreen;
import net.luis.game.type.GameType;
import net.luis.game.win.WinHandler;
import net.luis.netcore.packet.Packet;
import net.luis.utils.math.Mth;
import net.luis.utils.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public interface Game {
	
	default void init() {
		
	}
	
	default void start() {
		
	}
	
	@NotNull GameType<?> getType();
	
	@NotNull UUID getUniqueId();
	
	default @NotNull GameApplication getApplication() {
		if (FxApplication.getInstance() instanceof GameApplication application) {
			return application;
		}
		throw new IllegalStateException("Cannot get application because it is not a game application");
	}
	
	@NotNull GameMap getMap();
	
	@NotNull List<GamePlayer> getPlayers();
	
	@NotNull GameScreen getScreen();
	
	default @NotNull List<GamePlayer> getEnemies(@NotNull GamePlayer gamePlayer) {
		List<GamePlayer> enemies = Lists.newArrayList();
		for (GamePlayer player : this.getPlayers()) {
			if (!player.equals(gamePlayer)) {
				enemies.add(player);
			}
		}
		if (enemies.isEmpty()) {
			LogManager.getLogger(Game.class).warn("Fail to get enemies for player {}", gamePlayer.getName());
		}
		return enemies;
	}
	
	default GamePlayer getPlayerFor(@NotNull GameProfile profile) {
		for (GamePlayer gamePlayer : this.getPlayers()) {
			if (gamePlayer.getPlayer().getProfile().equals(profile)) {
				return gamePlayer;
			}
		}
		return null;
	}
	
	default GamePlayer getPlayerFor(@NotNull Player player) {
		return this.getPlayerFor(player.getProfile());
	}
	
	default GamePlayerType getPlayerType(@NotNull GamePlayer player) {
		for (GamePlayer gamePlayer : this.getPlayers()) {
			if (gamePlayer.equals(player)) {
				return gamePlayer.getPlayerType();
			}
		}
		return null;
	}
	
	default GamePlayerType getPlayerType(@NotNull Player player) {
		return this.getPlayerType(Objects.requireNonNull(this.getPlayerFor(player)));
	}
	
	GamePlayer getPlayer();
	
	void setPlayer(@NotNull GamePlayer player);
	
	default GamePlayer getStartPlayer() {
		this.nextPlayer(true);
		return ApplicationType.SERVER.isOn() ? this.getPlayer() : null;
	}
	
	default void nextPlayer(boolean random) {
		if (ApplicationType.SERVER.isOn()) {
			List<? extends GamePlayer> players = Lists.newArrayList(this.getPlayers());
			players.removeIf(this.getWinHandler().getWinOrder()::contains);
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
							LogManager.getLogger(Game.class).warn("Fail to get next player, since the player {} does not exists", player.getName());
							this.setPlayer(players.get(0));
						}
					}
				}
			} else {
				LogManager.getLogger(Game.class).warn("Unable to change player, since there is no player present");
			}
		}
	}
	
	default boolean removePlayer(@NotNull GamePlayer gamePlayer, boolean sendExit) {
		if (ApplicationType.SERVER.isOn()) {
			if (this.getPlayers().remove(gamePlayer)) {
				Player player = gamePlayer.getPlayer();
				if (sendExit) {
					Objects.requireNonNull(player.getConnection()).send(new ExitGamePacket());
				}
				LogManager.getLogger(Game.class).info("Removed player {} from game {}", player.getName(), this.getType().getName().toLowerCase());
				if (Objects.equals(this.getPlayer(), gamePlayer)) {
					this.nextPlayer(false);
				}
				if (!Mth.isInBounds(this.getPlayers().size(), this.getType().getMinPlayers(), this.getType().getMaxPlayers())) {
					this.stop();
				}
				gamePlayer.getPlayer().getScore().reset();
				this.broadcastPlayersExclude(new SyncPlayerDataPacket(player.getProfile(), player.isPlaying(), player.getScore()), gamePlayer);
				return true;
			} else {
				LogManager.getLogger(Game.class).warn("Fail to remove player {}, since the player does not playing game {}", gamePlayer.getName(), this.getType().getInfoName());
			}
		}
		return false;
	}
	
	default boolean isDiceGame() {
		return false;
	}
	
	default DiceHandler getDiceHandler() {
		return null;
	}
	
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
			LogManager.getLogger(Game.class).info("Start a new match of game {} with players {}", this.getType().getInfoName(), Utils.mapList(this.getPlayers(), GamePlayer::getName));
			return true;
		}
		LogManager.getLogger(Game.class).warn("Fail to start a new match of game {}, since the player count {} is not in bound {} - {} ", this.getType().getName().toLowerCase(), this.getPlayers().size(), this.getType().getMinPlayers(),
				this.getType().getMaxPlayers());
		return false;
	}
	
	default void stop() {
		LogManager.getLogger(Game.class).info("Stopping the current game {}", this.getType().getInfoName());
		for (GamePlayer gamePlayer : this.getPlayers()) {
			Player player = gamePlayer.getPlayer();
			player.getScore().reset();
			if (ApplicationType.SERVER.isOn()) {
				this.broadcastPlayer(new StopGamePacket(), gamePlayer);
				this.broadcastPlayersExclude(new SyncPlayerDataPacket(player.getProfile(), player.isPlaying(), player.getScore()), gamePlayer);
			}
		}
		this.getPlayers().clear();
		if (ApplicationType.SERVER.isOn()) {
			this.getApplication().getGameManager().removeGame(this);
		}
		LogManager.getLogger(Game.class).info("Game {} was successfully stopped", this.getType().getInfoName());
	}
	
	default void broadcastPlayer(@NotNull Packet packet, @NotNull GamePlayer gamePlayer) {
		gamePlayer.getPlayer().getConnection().send(packet);
	}
	
	default void broadcastPlayers(@NotNull Packet packet) {
		for (GamePlayer player : this.getPlayers()) {
			this.broadcastPlayer(packet, player);
		}
	}
	
	default void broadcastPlayersExclude(@NotNull Packet packet, @NotNull GamePlayer... gamePlayers) {
		for (GamePlayer player : this.getPlayers()) {
			if (!Lists.newArrayList(gamePlayers).contains(player)) {
				this.broadcastPlayer(packet, player);
			}
		}
	}
}
