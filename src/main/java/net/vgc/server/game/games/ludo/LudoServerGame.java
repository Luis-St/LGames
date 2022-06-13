package net.vgc.server.game.games.ludo;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Table.Cell;

import net.vgc.client.game.games.ludo.LudoClientGame;
import net.vgc.game.GameType;
import net.vgc.game.GameTypes;
import net.vgc.game.games.ludo.player.LudoPlayerType;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.network.packet.client.game.CurrentPlayerUpdatePacket;
import net.vgc.network.packet.client.game.StartGamePacket;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.player.GameProfile;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.game.ServerGame;
import net.vgc.server.game.dice.DiceHandler;
import net.vgc.server.game.games.ludo.dice.LudoDiceHandler;
import net.vgc.server.game.games.ludo.map.LudoServerMap;
import net.vgc.server.game.games.ludo.player.LudoServerPlayer;
import net.vgc.server.game.games.ludo.player.figure.LudoServerFigure;
import net.vgc.server.game.games.ludo.win.LudoWinHandler;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Mth;
import net.vgc.util.SimpleCell;
import net.vgc.util.Util;

public class LudoServerGame implements ServerGame {
	
	protected final DedicatedServer server;
	protected final LudoServerMap map;
	protected final List<LudoServerPlayer> players;
	protected final LudoDiceHandler diceHandler;
	protected final LudoWinHandler winHandler;
	protected LudoServerPlayer player;
	
	public LudoServerGame(DedicatedServer server, List<ServerPlayer> players) {
		this.server = server;
		this.map = new LudoServerMap(this.server, this);
		this.players = createGamePlayers(this, players, 4);
		this.diceHandler = new LudoDiceHandler(this, 1, 6);
		this.winHandler = new LudoWinHandler();
	}
	
	protected static List<LudoServerPlayer> createGamePlayers(LudoServerGame game, List<ServerPlayer> players, int figureCount) {
		if (!Mth.isInBounds(players.size(), 2, 4)) {
			LOGGER.error("Fail to create player type map for player list {} with size {}, since a player list with size in bounds 2 - 4 was expected", players.stream().map(game::getName).collect(Collectors.toList()));
			throw new IllegalStateException("Fail to create player type map for player list with size " + players.size() + ", since a player list with size in bounds 2 - 4 was expected");
		}
		LOGGER.info("Start game {} with players {}", game.getType().getInfoName(), Util.mapList(players, ServerPlayer::getProfile, GameProfile::getName));
		List<LudoServerPlayer> gamePlayers = Lists.newArrayList();
		int i = 0;
		for (ServerPlayer player : players) {
			LudoPlayerType playerType = LudoPlayerType.values()[i++];
			gamePlayers.add(new LudoServerPlayer(game, player, playerType, figureCount));
			
		}
		return gamePlayers;
	}
	
	@Override
	public void initGame() {
		this.map.init(this.players);
	}

	@Override
	public void startGame() {
		this.getPlayerList().broadcastAll(Util.mapList(this.players, LudoServerPlayer::getPlayer), new StartGamePacket(this.getType(), this.getPlayerInfos()));
	}
	
	protected List<Cell<GameProfile, GamePlayerType, List<UUID>>> getPlayerInfos() {
		List<Cell<GameProfile, GamePlayerType, List<UUID>>> playerInfos = Lists.newArrayList();
		for (LudoServerPlayer player : this.players) {
			playerInfos.add(new SimpleCell<>(player.getPlayer().getProfile(), player.getPlayerType(), Util.mapList(player.getFigures(), LudoServerFigure::getUUID)));
		}
		return playerInfos;
	}

	@Override
	public DedicatedServer getServer() {
		return this.server;
	}
	
	@Override
	public GameType<LudoServerGame, LudoClientGame> getType() {
		return GameTypes.LUDO;
	}

	@Override
	public LudoServerMap getMap() {
		return this.map;
	}

	@Override
	public List<LudoServerPlayer> getPlayers() {
		return this.players;
	}
	
	@Override
	public LudoServerPlayer getCurrentPlayer() {
		return this.player;
	}

	@Override
	public void setCurrentPlayer(GamePlayer player) {
		LOGGER.info("Update current player from {} to {}", Util.runIfNotNull(this.player, this::getName), Util.runIfNotNull(player, this::getName));
		this.player = (LudoServerPlayer) player;
		if (this.player != null) {
			this.server.getPlayerList().broadcastAll(Util.mapList(this.players, LudoServerPlayer::getPlayer), new CurrentPlayerUpdatePacket(this.player));
		}
	}
	
	@Override
	public boolean isDiceGame() {
		return true;
	}
	
	@Override
	public DiceHandler getDiceHandler() {
		return this.diceHandler;
	}
	
	@Override
	public LudoWinHandler getWinHandler() {
		return this.winHandler;
	}
	
	@Override
	public boolean nextMatch() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void handlePacket(ServerPacket serverPacket) {
		ServerGame.super.handlePacket(serverPacket);
		
	}
	
	@Override
	public String toString() {
		return "LudoServerGame";
	}
	
}
