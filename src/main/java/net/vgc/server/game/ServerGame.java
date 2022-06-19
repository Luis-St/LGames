package net.vgc.server.game;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;

import net.vgc.client.game.ClientGame;
import net.vgc.game.Game;
import net.vgc.game.GameType;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.score.PlayerScore;
import net.vgc.network.packet.Packet;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.client.SyncPlayerDataPacket;
import net.vgc.network.packet.client.game.ExitGamePacket;
import net.vgc.network.packet.client.game.StopGamePacket;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.player.GameProfile;
import net.vgc.player.Player;
import net.vgc.server.dedicated.DedicatedPlayerList;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.game.map.ServerGameMap;
import net.vgc.server.game.player.ServerGamePlayer;
import net.vgc.server.game.win.WinHandler;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Mth;
import net.vgc.util.Util;

public interface ServerGame extends Game, PacketHandler<ServerPacket> {
	
	@Override
	void initGame();
	
	@Override
	void startGame();
	
	DedicatedServer getServer();
	
	default DedicatedPlayerList getPlayerList() {
		return this.getServer().getPlayerList();
	}
	
	@Override
	GameType<? extends ServerGame, ? extends ClientGame> getType();
	
	@Override
	ServerGameMap getMap();
	
	List<? extends ServerGamePlayer> getPlayers();
	
	@Override
	default List<? extends ServerGamePlayer> getEnemies(GamePlayer player) {
		List<ServerGamePlayer> enemies = Lists.newArrayList();
		for (ServerGamePlayer gamePlayer : this.getPlayers()) {
			if (!gamePlayer.equals(player)) {
				enemies.add(gamePlayer);
			}
		}
		if (enemies.isEmpty()) {
			LOGGER.warn("Fail to get enemies for player {}", this.getName(player));
		}
		return enemies;
	}
	
	@Override
	default ServerGamePlayer getPlayerFor(Player player) {
		for (ServerGamePlayer gamePlayer : this.getPlayers()) {
			if (gamePlayer.getPlayer().equals(player)) {
				return gamePlayer;
			}
		}
		return null;
	}
	
	@Override
	default ServerGamePlayer getPlayerFor(GameProfile profile) {
		for (ServerGamePlayer gamePlayer : this.getPlayers()) {
			if (gamePlayer.getPlayer().getProfile().equals(profile)) {
				return gamePlayer;
			}
		}
		return null;
	}
	
	@Override
	ServerGamePlayer getCurrentPlayer();
	
	@Override
	void setCurrentPlayer(GamePlayer player);
	
	@Override
	default ServerGamePlayer getStartPlayer() {
		this.nextPlayer(true);
		return this.getCurrentPlayer();
	}
	
	@Override
	default boolean removePlayer(GamePlayer gamePlayer, boolean sendExit) {
		if (this.getPlayers().remove(gamePlayer)) {
			if (gamePlayer.getPlayer() instanceof ServerPlayer player) {
				if (sendExit) {
					player.connection.send(new ExitGamePacket());
				}
				player.setPlaying(false);
				LOGGER.info("Remove player {} from game {}", this.getName(player), this.getType().getName().toLowerCase());
				if (Objects.equals(this.getCurrentPlayer(), gamePlayer)) {
					this.nextPlayer(false);
				}
				if (!Mth.isInBounds(this.getPlayers().size(), this.getType().getMinPlayers(), this.getType().getMaxPlayers())) {
					this.stopGame();
				}
				this.getServer().getPlayerList().broadcastAllExclude(new SyncPlayerDataPacket(player.getProfile(), player.isPlaying(), new PlayerScore(player.getProfile())), player);
				return true;
			} else {
				LOGGER.warn("Fail to remove player {}, since the player is not a server player", this.getName(gamePlayer));
			}
		} else if (gamePlayer != null) {
			LOGGER.warn("Fail to remove player {}, since the player does not playing game {}", this.getName(gamePlayer), this.getType().getInfoName());
			if (gamePlayer.getPlayer().isPlaying()) {
				gamePlayer.getPlayer().setPlaying(false);
				LOGGER.info("Correcting the playing value of player {} to false, since it was not correctly reset", this.getName(gamePlayer));
			}
		}
		return false;
	}
	
	WinHandler getWinHandler();
	
	@Override
	boolean nextMatch();
	
	@Override
	default void stopGame() {
		for (ServerPlayer player : this.getServer().getPlayerList().getPlayers()) {
			if (Util.mapList(this.getPlayers(), GamePlayer::getPlayer).contains(player) && player.isPlaying()) {
				player.setPlaying(false);
			} else if (player.isPlaying()) {
				player.setPlaying(false);
				LOGGER.info("Correcting the playing value of player {} to false, since it was not correctly reset", this.getName(player));
			}
		}
		for (GamePlayer gamePlayer : this.getPlayers()) {
			if (gamePlayer.getPlayer() instanceof ServerPlayer player) {
				player.connection.send(new StopGamePacket());
				this.getPlayerList().broadcastAllExclude(new SyncPlayerDataPacket(player.getProfile(), player.isPlaying(), new PlayerScore(player.getProfile())), player);
			}
		}
		this.getPlayers().clear();
		this.getServer().setGame(null);
		LOGGER.info("Game {} was successfully stopped", this.getType().getInfoName());
	}
	
	@Override
	default void handlePacket(ServerPacket serverPacket) {
		this.getMap().handlePacket(serverPacket);
	}
	
	default void broadcastPlayer(Packet<?> packet, ServerGamePlayer player) {
		player.getPlayer().connection.send(packet);
	}
	
	default void broadcastPlayers(Packet<?> packet) {
		this.getPlayerList().broadcastAll(Util.mapList(this.getPlayers(), ServerGamePlayer::getPlayer), packet);
	}
	
}
