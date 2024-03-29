package net.vgc.server.game;

import com.google.common.collect.Lists;
import net.luis.utils.function.TriFunction;
import net.luis.utils.math.Mth;
import net.luis.utils.util.Utils;
import net.vgc.game.AbstractGame;
import net.vgc.game.Game;
import net.vgc.game.map.GameMap;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.win.WinHandler;
import net.vgc.network.packet.client.SyncPlayerDataPacket;
import net.vgc.network.packet.client.game.CurrentPlayerUpdatePacket;
import net.vgc.network.packet.client.game.ExitGamePacket;
import net.vgc.network.packet.client.game.StopGamePacket;
import net.vgc.player.Player;
import net.vgc.server.Server;
import net.vgc.server.player.ServerPlayer;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractServerGame extends AbstractGame {
	
	private final Server server;
	private final WinHandler winHandler;
	
	protected <T extends GamePlayerType> AbstractServerGame(Server server, BiFunction<Server, Game, GameMap> mapFunction, List<ServerPlayer> players, T[] playerTypes, TriFunction<Game, Player, T, GamePlayer> playerFunction, WinHandler winHandler) {
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
		LOGGER.info("Start game {} with players {}", game.getType().getInfoName(), Utils.mapList(players, Player::getName));
		List<GamePlayer> gamePlayers = Lists.newArrayList();
		int i = 0;
		for (ServerPlayer player : players) {
			T playerType = playerTypes[i++];
			gamePlayers.add(function.apply(game, player, playerType));
			
		}
		return gamePlayers;
	}
	
	public Server getServer() {
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
					player.connection.send(new ExitGamePacket());
				}
				player.setPlaying(false);
				LOGGER.info("Remove player {} from game {}", player.getName(), this.getType().getName().toLowerCase());
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
				LOGGER.warn("Fail to remove player {}, since the player is not a server player", gamePlayer.getName());
			}
		} else if (gamePlayer != null) {
			LOGGER.warn("Fail to remove player {}, since the player does not playing game {}", gamePlayer.getName(), this.getType().getInfoName());
			if (gamePlayer.getPlayer().isPlaying()) {
				gamePlayer.getPlayer().setPlaying(false);
				LOGGER.info("Correcting the playing value of player {} to false, since it was not correctly reset", gamePlayer.getName());
			}
		}
		return false;
	}
	
	@Override
	public WinHandler getWinHandler() {
		return this.winHandler;
	}
	
	@Override
	public void stop() {
		for (ServerPlayer player : this.getServer().getPlayerList().getPlayers()) {
			if (player.isPlaying()) {
				if (Utils.mapList(this.getPlayers(), GamePlayer::getPlayer).contains(player)) {
					player.setPlaying(false);
				} else {
					player.setPlaying(false);
					LOGGER.info("Correcting the playing value of player {} to false, since it was not correctly reset", player.getName());
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
