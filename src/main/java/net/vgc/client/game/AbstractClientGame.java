package net.vgc.client.game;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import net.luis.utils.function.QuadFunction;
import net.vgc.client.Client;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.client.screen.LobbyScreen;
import net.vgc.game.AbstractGame;
import net.vgc.game.Game;
import net.vgc.game.dice.DiceHandler;
import net.vgc.game.map.GameMap;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerInfo;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.win.WinHandler;
import net.vgc.network.packet.Packet;
import net.vgc.player.GameProfile;
import net.vgc.player.Player;
import net.vgc.util.Util;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractClientGame extends AbstractGame {
	
	private final Client client;
	
	protected AbstractClientGame(Client client, BiFunction<Client, Game, GameMap> mapFunction, List<GamePlayerInfo> playerInfos, QuadFunction<Game, Player, GamePlayerType, List<UUID>, GamePlayer> playerFunction) {
		super((game) -> {
			return mapFunction.apply(client, game);
		}, (game) -> {
			return createGamePlayers(client, game, playerInfos, playerFunction);
		});
		this.client = client;
	}
	
	protected static List<GamePlayer> createGamePlayers(Client client, Game game, List<GamePlayerInfo> playerInfos, QuadFunction<Game, Player, GamePlayerType, List<UUID>, GamePlayer> function) {
		LOGGER.info("Start game {} with players {}", game.getType().getInfoName(), Util.mapList(playerInfos, GamePlayerInfo::getProfile, GameProfile::getName));
		List<GamePlayer> gamePlayers = Lists.newArrayList();
		for (GamePlayerInfo playerInfo : playerInfos) {
			AbstractClientPlayer player = client.getPlayer(playerInfo.getProfile());
			if (player != null) {
				gamePlayers.add(function.apply(game, player, playerInfo.getPlayerType(), playerInfo.getUUIDs()));
			} else {
				LOGGER.warn("Fail to create game player for player {}, since the player does not exists on the client", playerInfo.getProfile().getName());
			}
		}
		return gamePlayers;
	}
	
	public Client getClient() {
		return this.client;
	}
	
	@Nullable
	@Override
	public final GamePlayer getStartPlayer() {
		LOGGER.warn("Can not get the start player on client");
		return null;
	}
	
	@Override
	public final void nextPlayer(boolean random) {
		LOGGER.warn("Can not set the next player on client");
	}
	
	@Override
	public final boolean removePlayer(GamePlayer player, boolean sendExit) {
		LOGGER.warn("Can not remove player {} from game {} on client", player.getPlayer().getProfile().getName(), this.getType().getInfoName());
		return false;
	}
	
	@Override
	public final DiceHandler getDiceHandler() {
		LOGGER.warn("Can not get the dice handler on client");
		return super.getDiceHandler();
	}
	
	@Override
	public final WinHandler getWinHandler() {
		LOGGER.warn("Can not get the win handler on client");
		return super.getWinHandler();
	}
	
	@Override
	public final boolean nextMatch() {
		LOGGER.warn("Can not start a next match from the client");
		return false;
	}
	
	@Override
	public void stop() {
		LOGGER.info("Stopping the current game {}", this.getType().getInfoName());
		for (AbstractClientPlayer player : this.client.getPlayers()) {
			player.setPlaying(false);
			player.getScore().reset();
		}
		this.client.setScreen(new LobbyScreen());
	}
	
	@Override
	public final void broadcastPlayer(Packet packet, GamePlayer gamePlayer) {
		LOGGER.warn("Can not broadcast packet {} to player {} on client", packet.getClass().getSimpleName(), gamePlayer.getName());
	}
	
}
