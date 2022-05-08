package net.vgc.game.ttt;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;

import net.vgc.game.Game;
import net.vgc.game.GameType;
import net.vgc.game.GameTypes;
import net.vgc.game.ttt.map.MutableTTTMap;
import net.vgc.game.ttt.map.TTTMap;
import net.vgc.player.GameProfile;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Util;

public class TTTGame implements Game {
	
	protected final List<ServerPlayer> players;
	protected final Map<ServerPlayer, TTTType> playerTypes;
	protected final MutableTTTMap mutableMap;
	protected ServerPlayer currentPlayer;
	
	public TTTGame(List<ServerPlayer> players) {
		this.players = players;
		this.playerTypes = createPlayerTypes(this.players);
		this.mutableMap = new MutableTTTMap();
	}
	
	protected static Map<ServerPlayer, TTTType> createPlayerTypes(List<ServerPlayer> players) {
		if (players.size() > 2) {
			LOGGER.error("Fail to create player type map for player list {} with size {}, since since a player list with size 2 was expected", players.stream().map(ServerPlayer::getProfile).map(GameProfile::getName).collect(Collectors.toList()));
			throw new IllegalStateException("Fail to create player type map for player list with size " + players.size() + ", since a player list with size 2 was expected");
		}
		Map<ServerPlayer, TTTType> playerTypes = Maps.newHashMap();
		playerTypes.put(players.get(0), TTTType.CROSS);
		playerTypes.put(players.get(1), TTTType.CIRCLE);
		return playerTypes;
	}
	
	@Override
	public GameType<TTTGame> getType() {
		return GameTypes.TIC_TAC_TOE;
	}

	@Override
	public List<ServerPlayer> getPlayers() {
		return this.players;
	}
	
	@Override
	public ServerPlayer getCurrentPlayer() {
		return this.currentPlayer;
	}

	@Override
	public void setCurrentPlayer(ServerPlayer currentPlayer) {
		LOGGER.info("Update current player from {} to {}", Util.runIfNotNull(this.currentPlayer, this::getName), Util.runIfNotNull(currentPlayer, this::getName));
		this.currentPlayer = currentPlayer;
	}
	
	protected String getName(ServerPlayer player) {
		return player.getProfile().getName();
	}
	
	public TTTType getPlayerType(ServerPlayer player) {
		if (this.players.contains(player)) {
			TTTType type = this.playerTypes.get(player);
			if (type != null) {
				return type;
			}
			LOGGER.warn("Fail to get Tic Tac Toe player type for player {}", player.getProfile().getName());
		} else {
			LOGGER.warn("Player {} is not playing game {}", player.getProfile().getName(), this.getType().getName().toLowerCase());
		}
		return TTTType.NO;
	}
	
	public TTTMap getMap() {
		return this.mutableMap.immutable();
	}
	
	public TTTMap handle(int vMap, int hMap, boolean callNext) {
		TTTType type = this.mutableMap.getType(vMap, hMap);
		if (type == TTTType.NO) {
			TTTType playerType = this.getPlayerType(this.currentPlayer);
			if (playerType != TTTType.NO) {
				this.mutableMap.setType(playerType, vMap, hMap);
			} else {
				LOGGER.warn("Fail to set field in map at pos {}:{}, since player {} has no {} type", vMap, hMap, this.currentPlayer.getProfile().getName(), this.getType().getName().toLowerCase());
			}
		} else {
			LOGGER.warn("The field in map at pos {}:{} is already set to {}", vMap, hMap, type);
		}
		if (callNext) {
			this.nextPlayer();
		}
		return this.mutableMap;
	}
	
}
