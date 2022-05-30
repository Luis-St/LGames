package net.vgc.server.game.games.ttt;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.vgc.client.game.games.ttt.TTTClientGame;
import net.vgc.game.GameType;
import net.vgc.game.GameTypes;
import net.vgc.game.games.ttt.player.TTTPlayerType;
import net.vgc.game.player.GamePlayer;
import net.vgc.network.packet.client.game.CurrentPlayerUpdatePacket;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.game.ServerGame;
import net.vgc.server.game.games.ttt.map.TTTServerMap;
import net.vgc.server.game.games.ttt.player.TTTServerPlayer;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Mth;
import net.vgc.util.Util;

public class TTTServerGame implements ServerGame {
	
	protected final DedicatedServer server;
	protected final TTTServerMap map;
	protected final List<TTTServerPlayer> players;
	protected TTTServerPlayer player;
	
	public TTTServerGame(DedicatedServer server, List<ServerPlayer> players) {
		this.server = server;
		this.map = new TTTServerMap();
		this.players = createGamePlayers(this, players);
	}
	
	protected static List<TTTServerPlayer> createGamePlayers(TTTServerGame game, List<ServerPlayer> players) {
		if (players.size() != 2) {
			LOGGER.error("Fail to create player type map for player list {} with size {}, since a player list with size in bounds 2 was expected", players.stream().map(game::getName).collect(Collectors.toList()));
			throw new IllegalStateException("Fail to create player type map for player list with size " + players.size() + ", since a player list with size 2 was expected");
		}
		List<TTTServerPlayer> gamePlayers = Lists.newArrayList();
		gamePlayers.add(new TTTServerPlayer(game, players.get(0), TTTPlayerType.CROSS));
		gamePlayers.add(new TTTServerPlayer(game, players.get(1), TTTPlayerType.CIRCLE));
		return gamePlayers;
	}
	
	@Override
	public void initGame() {
		
	}

	@Override
	public void startGame() {
		
	}
	
	@Override
	public DedicatedServer getServer() {
		return this.server;
	}

	@Override
	public GameType<TTTServerGame, TTTClientGame> getType() {
		return GameTypes.TIC_TAC_TOE;
	}
	
	@Override
	public TTTServerMap getMap() {
		return this.map;
	}
	
	@Override
	public List<TTTServerPlayer> getPlayers() {
		return this.players;
	}
	
	@Override
	public TTTServerPlayer getCurrentPlayer() {
		return this.player;
	}
	
	@Override
	public void setCurrentPlayer(GamePlayer player) {
		LOGGER.info("Update current player from {} to {}", Util.runIfNotNull(this.player, this::getName), Util.runIfNotNull(player, this::getName));
		this.player = (TTTServerPlayer) player;
		if (this.player != null) {
			this.server.getPlayerList().broadcastAll(Util.mapList(this.players, TTTServerPlayer::getPlayer), new CurrentPlayerUpdatePacket(this.player));
		}
	}

	@Override
	public boolean nextMatch() {
		if (Mth.isInBounds(this.players.size(), this.getType().getMinPlayers(), this.getType().getMaxPlayers())) {
			this.map.reset();
			this.nextPlayer(true);
//			playerList.broadcastAll(this.players, new UpdateTTTGamePacket(this.mutableMap.immutable())); // TODO: update map
			LOGGER.info("Start a new match of game {} with players {}", this.getType().getInfoName(), Util.mapList(this.players, this::getName));
			return true;
		}
		LOGGER.warn("Fail to start a new match of game {}, since the player count {} is not in bound {} - {} ", this.getType().getName().toLowerCase(), this.players.size(), this.getType().getMinPlayers(), this.getType().getMaxPlayers());
		return false;
	}
	
	@Override
	public void handlePacket(ServerPacket packet) {
		ServerGame.super.handlePacket(packet);
		
	}
	
	@Override
	public String toString() {
		return "TTTServerGame";
	}

}
