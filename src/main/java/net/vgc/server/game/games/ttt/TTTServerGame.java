package net.vgc.server.game.games.ttt;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.vgc.client.game.games.ttt.TTTClientGame;
import net.vgc.game.GameResult;
import net.vgc.game.GameType;
import net.vgc.game.GameTypes;
import net.vgc.game.games.ttt.TTTResultLine;
import net.vgc.game.games.ttt.map.field.TTTFieldPos;
import net.vgc.game.games.ttt.player.TTTPlayerType;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.score.PlayerScore;
import net.vgc.network.packet.client.SyncPlayerDataPacket;
import net.vgc.network.packet.client.game.CurrentPlayerUpdatePacket;
import net.vgc.network.packet.client.game.GameActionFailedPacket;
import net.vgc.network.packet.client.game.TTTGameResultPacket;
import net.vgc.network.packet.client.game.UpdateGameMapPacket;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.network.packet.server.game.SelectGameFieldPacket;
import net.vgc.player.GameProfile;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.game.ServerGame;
import net.vgc.server.game.games.ttt.map.TTTServerMap;
import net.vgc.server.game.games.ttt.map.field.TTTServerField;
import net.vgc.server.game.games.ttt.player.TTTServerPlayer;
import net.vgc.server.game.games.ttt.player.figure.TTTServerFigure;
import net.vgc.server.game.games.ttt.win.TTTWinHandler;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Mth;
import net.vgc.util.Util;

public class TTTServerGame implements ServerGame {
	
	protected final DedicatedServer server;
	protected final TTTServerMap map;
	protected final List<TTTServerPlayer> players;
	protected final TTTWinHandler winHandler;
	protected TTTServerPlayer player;
	
	public TTTServerGame(DedicatedServer server, List<ServerPlayer> players) {
		this.server = server;
		this.map = new TTTServerMap(this.server, this);
		this.players = createGamePlayers(this, players);
		this.winHandler = new TTTWinHandler();
	}
	
	protected static List<TTTServerPlayer> createGamePlayers(TTTServerGame game, List<ServerPlayer> players) {
		if (players.size() != 2) {
			LOGGER.error("Fail to create player type map for player list {} with size {}, since a player list with size in bounds 2 was expected", players.stream().map(game::getName).collect(Collectors.toList()));
			throw new IllegalStateException("Fail to create player type map for player list with size " + players.size() + ", since a player list with size 2 was expected");
		}
		LOGGER.info("Start game {} with players {}", game.getType().getInfoName(), Util.mapList(players, ServerPlayer::getProfile, GameProfile::getName));
		List<TTTServerPlayer> gamePlayers = Lists.newArrayList();
		gamePlayers.add(new TTTServerPlayer(game, players.get(0), TTTPlayerType.CROSS));
		gamePlayers.add(new TTTServerPlayer(game, players.get(1), TTTPlayerType.CIRCLE));
		return gamePlayers;
	}
	
	@Override
	public void initGame() {
		this.map.init(this.players);
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
	public TTTWinHandler getWinHandler() {
		return this.winHandler;
	}

	@Override
	public boolean nextMatch() {
		if (Mth.isInBounds(this.players.size(), this.getType().getMinPlayers(), this.getType().getMaxPlayers())) {
			this.map.reset();
			this.winHandler.reset();
			this.nextPlayer(true);
			this.broadcastPlayers(new UpdateGameMapPacket(Util.mapList(this.getMap().getFields(), TTTServerField::getFieldInfo)));
			LOGGER.info("Start a new match of game {} with players {}", this.getType().getInfoName(), Util.mapList(this.players, this::getName));
			return true;
		}
		LOGGER.warn("Fail to start a new match of game {}, since the player count {} is not in bound {} - {} ", this.getType().getName().toLowerCase(), this.players.size(), this.getType().getMinPlayers(), this.getType().getMaxPlayers());
		return false;
	}
	
	@Override
	public void handlePacket(ServerPacket serverPacket) {
		ServerGame.super.handlePacket(serverPacket);
		if (serverPacket instanceof SelectGameFieldPacket packet) {
			TTTFieldPos fieldPos = (TTTFieldPos) packet.getFieldPos();
			TTTServerPlayer player = (TTTServerPlayer) this.getPlayerFor(packet.getProfile());
			if (Objects.equals(this.player, player)) {
				TTTServerField field = this.map.getField(null, null, fieldPos);
				if (field != null) {
					if (field.isEmpty()) {
						TTTServerFigure figure = player.getUnplacedFigure();
						if (figure != null) {
							field.setFigure(figure);
							this.broadcastPlayers(new UpdateGameMapPacket(Util.mapList(this.getMap().getFields(), TTTServerField::getFieldInfo)));
							if (this.winHandler.hasPlayerFinished(player)) {
								this.winHandler.onPlayerFinished(player);
								LOGGER.info("Finished game {} with player win order: {}", this.getType().getInfoName(), Util.mapList(this.winHandler.getWinOrder(), this::getName));
								TTTResultLine resultLine = this.winHandler.getResultLine(map);
								if (resultLine != TTTResultLine.EMPTY) {
									for (TTTServerPlayer gamePlayer : this.players) {
										if (gamePlayer.equals(player))  {
											this.handlePlayerGameResult(gamePlayer, GameResult.WIN, resultLine, PlayerScore::increaseWin);
										} else {
											this.handlePlayerGameResult(gamePlayer, GameResult.LOSE, resultLine, PlayerScore::increaseLose);
										}
									}
								} else {
									LOGGER.warn("Player {} finished the game but there is no result line", this.getName(player));
									this.nextPlayer(false);
								}
							} else if (this.winHandler.isDraw(this.map)) {
								this.broadcastPlayers(new TTTGameResultPacket(GameResult.DRAW, TTTResultLine.EMPTY));
								for (TTTServerPlayer gamePlayer : players) {
									this.handlePlayerGameResult(gamePlayer, GameResult.DRAW, TTTResultLine.EMPTY, PlayerScore::increaseDraw);
								}
							} else {
								this.nextPlayer(false);
							}
						} else {
							LOGGER.warn("Fail to get unplaced figure for player {}, since all figures have been placed", this.getName(player));
							this.stopGame();
						}
					} else {
						LOGGER.warn("Fail to place a figure of player {} on field, since on the field is already a figure of type {}", this.getName(player), field.getFigure().getPlayerType());
						this.broadcastPlayer(new GameActionFailedPacket(), player);
					}
				} else {
					LOGGER.warn("Fail to get field for pos {}", fieldPos.getPosition());
					this.broadcastPlayer(new GameActionFailedPacket(), player);
				}
			} else {
				LOGGER.warn("Player {} tries to change the {} map at pos {} to {}, but it is not his turn", this.getName(player), fieldPos.getPosition(), player.getPlayerType());
			}
		}
	}
	
	protected void handlePlayerGameResult(TTTServerPlayer gamePlayer, GameResult result, TTTResultLine resultLine, Consumer<PlayerScore> consumer) {
		ServerPlayer player = gamePlayer.getPlayer();
		this.broadcastPlayer(new TTTGameResultPacket(result, resultLine), gamePlayer);
		consumer.accept(player.getScore());
		this.broadcastPlayers(new SyncPlayerDataPacket(player.getProfile(), true, player.getScore()));
	}
	
	@Override
	public String toString() {
		return "TTTServerGame";
	}

}
