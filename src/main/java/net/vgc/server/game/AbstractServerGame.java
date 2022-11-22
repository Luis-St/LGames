package net.vgc.server.game;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import com.google.common.collect.Lists;

import net.vgc.game.AbstractGame;
import net.vgc.game.Game;
import net.vgc.game.action.data.gobal.EmptyData;
import net.vgc.game.action.type.ActionTypes;
import net.vgc.game.map.GameMap;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.network.packet.client.SyncPlayerDataPacket;
import net.vgc.network.packet.client.game.CurrentPlayerUpdatePacket;
import net.vgc.network.packet.client.game.StopGamePacket;
import net.vgc.player.Player;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.game.win.WinHandler;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Mth;
import net.vgc.util.Util;
import net.vgc.util.function.TriFunction;

public abstract class AbstractServerGame extends AbstractGame {
	
	private final DedicatedServer server;
	private final WinHandler winHandler;
	
	protected <T extends GamePlayerType> AbstractServerGame(DedicatedServer server, BiFunction<DedicatedServer, Game, GameMap> mapFunction, List<ServerPlayer> players, T[] playerTypes, TriFunction<Game, Player, T, GamePlayer> playerFunction,
		WinHandler winHandler) {
		super((game) -> {
			return mapFunction.apply(server, game);
		}, (game) -> {
			return createGamePlayers(game, players, playerTypes, playerFunction);
		});
		this.server = server;
		this.winHandler = winHandler;
	}
	
	private static <T extends GamePlayerType> List<GamePlayer> createGamePlayers(Game game, List<ServerPlayer> players, T[] playerTypes, TriFunction<Game, Player, T, GamePlayer> function) {
		if (!game.getType().hasEnoughPlayers(players.size())) {
			LOGGER.error("Fail to create game players list with size {}, since a player list with size in bounds {} was expected", players.size(), game.getType().getBounds());
			throw new IllegalStateException("Fail to create game players list with size " + players.size() + ", since a player list with size in bounds " + game.getType().getBounds() + " was expected");
		}
		if (players.size() > playerTypes.length) {
			LOGGER.error("Fail to create game players list, since there are {} player types present but at least {} are required", playerTypes.length, players.size());
			throw new IllegalStateException("Fail to create game players list, since there are " + playerTypes.length + " player types present but at least " + players.size() + " are required");
		}
		LOGGER.info("Start game {} with players {}", game.getType().getInfoName(), Util.mapList(players, game::getName));
		List<GamePlayer> gamePlayers = Lists.newArrayList();
		int i = 0;
		for (ServerPlayer player : players) {
			T playerType = playerTypes[i++];
			gamePlayers.add(function.apply(game, player, playerType));
			
		}
		return gamePlayers;
	}
	
	public DedicatedServer getServer() {
		return this.server;
	}
	
	@Override
	public void setPlayer(GamePlayer player) {
		super.setPlayer(player);
		if (this.getPlayer() != null) {
			this.broadcastPlayers(new CurrentPlayerUpdatePacket(this.getPlayer()));
		}
	}
	
	@Override
	public boolean removePlayer(GamePlayer gamePlayer, boolean sendExit) {
		if (this.getPlayers().remove(gamePlayer)) {
			if (gamePlayer.getPlayer() instanceof ServerPlayer player) {
				if (sendExit) {
					ActionTypes.EXIT_GAME.send(player.connection, new EmptyData());
				}
				player.setPlaying(false);
				LOGGER.info("Remove player {} from game {}", this.getName(player), this.getType().getName().toLowerCase());
				if (Objects.equals(this.getPlayer(), gamePlayer)) {
					this.nextPlayer(false);
				}
				if (!Mth.isInBounds(this.getPlayers().size(), this.getType().getMinPlayers(), this.getType().getMaxPlayers())) {
					this.stopGame();
				}
				this.broadcastPlayers(null);
				player.getScore().reset();
				this.broadcastPlayersExclude(new SyncPlayerDataPacket(player), gamePlayer);
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
	
	@Override
	public WinHandler getWinHandler() {
		return this.winHandler;
	}
	
	@Override
	public void stopGame() {
		for (ServerPlayer player : this.getServer().getPlayerList().getPlayers()) {
			if (player.isPlaying()) {
				if (Util.mapList(this.getPlayers(), GamePlayer::getPlayer).contains(player)) {
					player.setPlaying(false);
				} else {
					player.setPlaying(false);
					LOGGER.info("Correcting the playing value of player {} to false, since it was not correctly reset", this.getName(player));
				}
			}
		}
		for (GamePlayer gamePlayer : this.getPlayers()) {
			this.broadcastPlayer(new StopGamePacket(), gamePlayer);
			gamePlayer.getPlayer().getScore().reset();
			this.broadcastPlayersExclude(new SyncPlayerDataPacket(gamePlayer), gamePlayer);
		}
		this.getPlayers().clear();
		this.getServer().setGame(null);
		LOGGER.info("Game {} was successfully stopped", this.getType().getInfoName());
	}
	
}
