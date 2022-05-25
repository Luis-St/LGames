package net.vgc.game.ludo;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table.Cell;

import net.vgc.game.Game;
import net.vgc.game.GameType;
import net.vgc.game.GameTypes;
import net.vgc.game.ludo.dice.LudoDiceHandler;
import net.vgc.game.ludo.map.LudoMap;
import net.vgc.game.ludo.map.field.LudoField;
import net.vgc.game.ludo.map.field.LudoFieldPos;
import net.vgc.game.ludo.map.field.LudoFieldType;
import net.vgc.game.ludo.player.LudoFigure;
import net.vgc.game.ludo.player.LudoPlayer;
import net.vgc.game.score.GameScore;
import net.vgc.network.packet.client.game.CurrentPlayerUpdatePacket;
import net.vgc.network.packet.client.game.UpdateLudoGamePacket;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Mth;
import net.vgc.util.Util;

public class LudoGame implements Game {
	
	protected final List<ServerPlayer> players;
	protected final GameScore score;
	protected final Map<ServerPlayer, LudoType> playerTypes;
	protected final Map<ServerPlayer, LudoPlayer> ludoPlayers;
	protected final LudoMap map;
	protected final LudoDiceHandler diceHandler;
	protected ServerPlayer currentPlayer;
	
	public LudoGame(List<ServerPlayer> players) {
		this.players = players;
		this.score = new GameScore(this);
		this.playerTypes = createPlayerTypes(this.players);
		this.ludoPlayers = createLudoPlayers(this.playerTypes, 4);
		this.map = new LudoMap();
		this.diceHandler = new LudoDiceHandler(this, 1, 6);
	}
	
	protected static Map<ServerPlayer, LudoType> createPlayerTypes(List<ServerPlayer> players) {
		if (!Mth.isInBounds(players.size(), 2, 4)) {
			LOGGER.error("Fail to create player type map for player list {} with size {}, since since a player list with size in bounds 2 - 4 was expected", players.stream().map(LudoGame::getName).collect(Collectors.toList()));
			throw new IllegalStateException("Fail to create player type map for player list with size " + players.size() + ", since a player list with size 2 was expected");
		}
		Map<ServerPlayer, LudoType> playerTypes = Maps.newHashMap();
		int i = 0;
		for (ServerPlayer player : players) {
			playerTypes.put(player, LudoType.values()[i++]);
			
		}
		return playerTypes;
	}
	
	protected static Map<ServerPlayer, LudoPlayer> createLudoPlayers(Map<ServerPlayer, LudoType> playerTypes, int figureCount) {
		Map<ServerPlayer, LudoPlayer> ludoPlayers = Maps.newHashMap();
		for (Entry<ServerPlayer, LudoType> entry : playerTypes.entrySet()) {
			ServerPlayer player = entry.getKey();
			ludoPlayers.put(player, new LudoPlayer(player, entry.getValue(), figureCount));
		}
		return ludoPlayers;
	}
	
	@Override
	public GameType<LudoGame> getType() {
		return GameTypes.LUDO;
	}
	
	@Override
	public List<ServerPlayer> getPlayers() {
		return this.players;
	}

	@Override
	public GameScore getScore() {
		return this.score;
	}

	@Override
	public ServerPlayer getCurrentPlayer() {
		return this.currentPlayer;
	}

	@Override
	public void setCurrentPlayer(ServerPlayer currentPlayer) {
		LOGGER.info("Update current player from {} to {}", Util.runIfNotNull(this.currentPlayer, LudoGame::getName), Util.runIfNotNull(currentPlayer, LudoGame::getName));
		this.currentPlayer = currentPlayer;
		this.diceHandler.resetRollCount();
		if (this.currentPlayer != null) {
			this.getServer().getPlayerList().broadcastAll(this.players, new CurrentPlayerUpdatePacket(this.currentPlayer.getProfile()));
		}
	}
	
	protected static String getName(ServerPlayer player) {
		return player.getProfile().getName();
	}
	
	@Override
	public LudoType getPlayerType(ServerPlayer player) {
		return this.playerTypes.get(player);
	}
	
	public Map<ServerPlayer, LudoType> getPlayerTypes() {
		return this.playerTypes;
	}
	
	@Nullable
	public LudoPlayer getLudoPlayer(ServerPlayer player) {
		return this.ludoPlayers.get(player);
	}
	
	public List<LudoFigure> getFigures() {
		List<LudoFigure> figures = Lists.newArrayList();
		for (Entry<ServerPlayer, LudoPlayer> entry : this.ludoPlayers.entrySet()) {
			figures.addAll(entry.getValue().getFigures());
		}
		return figures;
	}
	
	public LudoMap getMap() {
		return this.map;
	}

	@Override
	public boolean nextMatch() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public List<Cell<LudoFieldType, LudoFieldPos, LudoFigure>> handle(LudoFieldPos pos) {
		LudoPlayer player = this.getLudoPlayer(this.currentPlayer);
		if (player != null) {
			LudoFigure figure = player.getFigureFromField(this.map, pos);
			int count = this.diceHandler.getLastCount(this.currentPlayer);
			if (count > 0) {
				LudoField field = this.map.getNextField(figure, count);
				if (this.map.canMoveFigure(player, figure, count)) {
					if (!this.map.moveFigure(figure, count)) {
						LOGGER.warn("Fail to move figure {} of player {} to field {}", figure.getCount(), figure.getProfile().getName(), field != null ? field.getPos().getGreen() : "null");
					}
				} else {
					LOGGER.warn("The player {} can not move figure {} to field {}", figure.getProfile().getName(), figure.getCount(), field != null ? field.getPos().getGreen() : "null");
				}
			}
		}
		return this.map.getFigurePositions(this.getFigures());
	}
	
	@Override
	public boolean isDiceGame() {
		return true;
	}
	
	@Override
	public LudoDiceHandler getDiceHandler() {
		return this.diceHandler;
	}
	
	@Override
	public void onStarted() {
		this.map.init(Lists.newArrayList(this.ludoPlayers.values()));
		this.broadcastPlayers(new UpdateLudoGamePacket(this.map.getFigurePositions(this.getFigures())));
	}

}
