package net.luis.client.game;

import com.google.common.collect.Lists;
import net.luis.client.Client;
import net.luis.client.player.AbstractClientPlayer;
import net.luis.client.screen.LobbyScreen;
import net.luis.game.AbstractGame;
import net.luis.game.Game;
import net.luis.game.dice.DiceHandler;
import net.luis.game.map.GameMap;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.GamePlayerInfo;
import net.luis.game.player.GamePlayerType;
import net.luis.game.win.WinHandler;
import net.luis.network.packet.Packet;
import net.luis.player.GameProfile;
import net.luis.player.Player;
import net.luis.utils.function.QuadFunction;
import net.luis.utils.util.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

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
	
	private static List<GamePlayer> createGamePlayers(Client client, Game game, List<GamePlayerInfo> playerInfos, QuadFunction<Game, Player, GamePlayerType, List<UUID>, GamePlayer> function) {
		Game.LOGGER.info("Start game {} with players {}", game.getType().getInfoName(), Utils.mapList(playerInfos, GamePlayerInfo::getProfile, GameProfile::getName));
		List<GamePlayer> gamePlayers = Lists.newArrayList();
		for (GamePlayerInfo playerInfo : playerInfos) {
			AbstractClientPlayer player = client.getPlayer(playerInfo.getProfile());
			if (player != null) {
				gamePlayers.add(function.apply(game, player, playerInfo.getPlayerType(), playerInfo.getUUIDs()));
			} else {
				Game.LOGGER.warn("Fail to create game player for player {}, since the player does not exists on the client", playerInfo.getProfile().getName());
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
		Game.LOGGER.warn("Can not get the start player on client");
		return null;
	}
	
	@Override
	public final void nextPlayer(boolean random) {
		Game.LOGGER.warn("Can not set the next player on client");
	}
	
	@Override
	public final boolean removePlayer(GamePlayer player, boolean sendExit) {
		Game.LOGGER.warn("Can not remove player {} from game {} on client", player.getPlayer().getProfile().getName(), this.getType().getInfoName());
		return false;
	}
	
	@Override
	public final DiceHandler getDiceHandler() {
		Game.LOGGER.warn("Can not get the dice handler on client");
		return super.getDiceHandler();
	}
	
	@Override
	public final WinHandler getWinHandler() {
		Game.LOGGER.warn("Can not get the win handler on client");
		return super.getWinHandler();
	}
	
	@Override
	public final boolean nextMatch() {
		Game.LOGGER.warn("Can not start a next match from the client");
		return false;
	}
	
	@Override
	public void stop() {
		Game.LOGGER.info("Stopping the current game {}", this.getType().getInfoName());
		for (AbstractClientPlayer player : this.client.getPlayers()) {
			player.setPlaying(false);
			player.getScore().reset();
		}
		this.client.setScreen(new LobbyScreen());
	}
	
	@Override
	public final void broadcastPlayer(Packet packet, GamePlayer gamePlayer) {
		Game.LOGGER.warn("Can not broadcast packet {} to player {} on client", packet.getClass().getSimpleName(), gamePlayer.getName());
	}
	
}
