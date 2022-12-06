package net.vgc.game;

import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import net.luis.utils.math.Mth;
import net.vgc.client.game.AbstractClientGame;
import net.vgc.game.dice.DiceHandler;
import net.vgc.game.map.GameMap;
import net.vgc.game.map.field.GameField;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.type.GameType;
import net.vgc.game.win.WinHandler;
import net.vgc.network.packet.Packet;
import net.vgc.network.packet.client.game.UpdateGameMapPacket;
import net.vgc.player.GameProfile;
import net.vgc.player.Player;
import net.vgc.server.game.AbstractServerGame;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Util;

/**
 *
 * @author Luis-st
 *
 */

public interface Game {
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	default void init() {
		
	}
	
	default void start() {
		
	}
	
	GameType<? extends AbstractServerGame, ? extends AbstractClientGame> getType();
	
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
		return this.getPlayer();
	}
	
	default void nextPlayer(boolean random) {
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
	}
	
	boolean removePlayer(GamePlayer player, boolean sendExit);
	
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
				this.getDiceHandler().reset();
			}
			this.getWinHandler().reset();
			this.nextPlayer(true);
			this.broadcastPlayers(new UpdateGameMapPacket(Util.mapList(this.getMap().getFields(), GameField::getFieldInfo)));
			LOGGER.info("Start a new match of game {} with players {}", this.getType().getInfoName(), Util.mapList(this.getPlayers(), GamePlayer::getName));
			return true;
		}
		LOGGER.warn("Fail to start a new match of game {}, since the player count {} is not in bound {} - {} ", this.getType().getName().toLowerCase(), this.getPlayers().size(), this.getType().getMinPlayers(), this.getType().getMaxPlayers());
		return false;
	}
	
	void stop();
	
	default void broadcastPlayer(Packet<?> packet, GamePlayer gamePlayer) {
		if (gamePlayer.getPlayer() instanceof ServerPlayer player) {
			player.connection.send(packet);
		}
	}
	
	default void broadcastPlayers(Packet<?> packet) {
		for (GamePlayer player : this.getPlayers()) {
			this.broadcastPlayer(packet, player);
		}
	}
	
	default void broadcastPlayersExclude(Packet<?> packet, GamePlayer... gamePlayers) {
		for (GamePlayer player : this.getPlayers()) {
			if (!Lists.newArrayList(gamePlayers).contains(player)) {
				this.broadcastPlayer(packet, player);
			}
		}
	}
	
}
