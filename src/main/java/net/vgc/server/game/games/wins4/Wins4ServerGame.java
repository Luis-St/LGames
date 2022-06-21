package net.vgc.server.game.games.wins4;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.vgc.client.game.games.wins4.Wins4ClientGame;
import net.vgc.game.GameResult;
import net.vgc.game.GameType;
import net.vgc.game.GameTypes;
import net.vgc.game.games.wins4.Wins4ResultLine;
import net.vgc.game.games.wins4.map.field.Wins4FieldPos;
import net.vgc.game.games.wins4.player.Wins4PlayerType;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.score.PlayerScore;
import net.vgc.network.packet.client.SyncPlayerDataPacket;
import net.vgc.network.packet.client.game.CurrentPlayerUpdatePacket;
import net.vgc.network.packet.client.game.GameActionFailedPacket;
import net.vgc.network.packet.client.game.UpdateGameMapPacket;
import net.vgc.network.packet.client.game.Wins4GameResultPacket;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.network.packet.server.game.SelectGameFieldPacket;
import net.vgc.player.GameProfile;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.game.ServerGame;
import net.vgc.server.game.games.wins4.map.Wins4ServerMap;
import net.vgc.server.game.games.wins4.map.field.Wins4ServerField;
import net.vgc.server.game.games.wins4.player.Wins4ServerPlayer;
import net.vgc.server.game.games.wins4.player.figure.Wins4ServerFigure;
import net.vgc.server.game.games.wins4.win.Wins4WinHandler;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Mth;
import net.vgc.util.Util;

public class Wins4ServerGame implements ServerGame {
	
	protected final DedicatedServer server;
	protected final Wins4ServerMap map;
	protected final List<Wins4ServerPlayer> players;
	protected final Wins4WinHandler winHandler;
	protected Wins4ServerPlayer player;
	
	public Wins4ServerGame(DedicatedServer server, List<ServerPlayer> players) {
		this.server = server;
		this.map = new Wins4ServerMap(this);
		this.players = createGamePlayers(this, players);
		this.winHandler = new Wins4WinHandler();
	}
	
	protected static List<Wins4ServerPlayer> createGamePlayers(Wins4ServerGame game, List<ServerPlayer> players) {
		if (players.size() != 2) {
			LOGGER.error("Fail to create player type map for player list {} with size {}, since a player list with size in bounds 2 was expected", players.stream().map(game::getName).collect(Collectors.toList()));
			throw new IllegalStateException("Fail to create player type map for player list with size " + players.size() + ", since a player list with size 2 was expected");
		}
		LOGGER.info("Start game {} with players {}", game.getType().getInfoName(), Util.mapList(players, ServerPlayer::getProfile, GameProfile::getName));
		List<Wins4ServerPlayer> gamePlayers = Lists.newArrayList();
		gamePlayers.add(new Wins4ServerPlayer(game, players.get(0), Wins4PlayerType.YELLOW));
		gamePlayers.add(new Wins4ServerPlayer(game, players.get(1), Wins4PlayerType.RED));
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
	public GameType<Wins4ServerGame, Wins4ClientGame> getType() {
		return GameTypes.WINS_4;
	}

	@Override
	public Wins4ServerMap getMap() {
		return this.map;
	}

	@Override
	public List<Wins4ServerPlayer> getPlayers() {
		return this.players;
	}

	@Override
	public Wins4ServerPlayer getCurrentPlayer() {
		return this.player;
	}

	@Override
	public void setCurrentPlayer(GamePlayer player) {
		LOGGER.info("Update current player from {} to {}", Util.runIfNotNull(this.player, this::getName), Util.runIfNotNull(player, this::getName));
		this.player = (Wins4ServerPlayer) player;
		if (this.player != null) {
			this.server.getPlayerList().broadcastAll(Util.mapList(this.players, Wins4ServerPlayer::getPlayer), new CurrentPlayerUpdatePacket(this.player));
		}
	}

	@Override
	public Wins4WinHandler getWinHandler() {
		return this.winHandler;
	}

	@Override
	public boolean nextMatch() {
		if (Mth.isInBounds(this.players.size(), this.getType().getMinPlayers(), this.getType().getMaxPlayers())) {
			this.map.reset();
			this.winHandler.reset();
			this.nextPlayer(true);
			this.broadcastPlayers(new UpdateGameMapPacket(Util.mapList(this.getMap().getFields(), Wins4ServerField::getFieldInfo)));
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
			Wins4FieldPos fieldPos = (Wins4FieldPos) packet.getFieldPos();
			Wins4ServerPlayer player = (Wins4ServerPlayer) this.getPlayerFor(packet.getProfile());
			if (Objects.equals(this.player, player)) {
				Optional<Wins4ServerField> optionalField = Util.reverseList(this.map.getFieldsForColumn(fieldPos.getColumn())).stream().filter(Wins4ServerField::isEmpty).findFirst();
				if (optionalField.isPresent()) {
					Wins4ServerField field = optionalField.orElseThrow(NullPointerException::new);
					if (field.isEmpty()) {
						Wins4ServerFigure figure = player.getUnplacedFigure();
						if (figure != null) {
							field.setFigure(figure);
							this.broadcastPlayers(new UpdateGameMapPacket(Util.mapList(this.getMap().getFields(), Wins4ServerField::getFieldInfo)));
							if (this.winHandler.hasPlayerFinished(player)) {
								this.winHandler.onPlayerFinished(player);
								LOGGER.info("Finished game {} with player win order: {}", this.getType().getInfoName(), Util.mapList(this.winHandler.getWinOrder(), this::getName));
								Wins4ResultLine resultLine = this.winHandler.getResultLine(this.map);
								LOGGER.debug("Result line of player {} is {}", this.getName(player), resultLine);
								if (resultLine != Wins4ResultLine.EMPTY) {
									for (Wins4ServerPlayer gamePlayer : this.players) {
										if (gamePlayer.equals(player))  {
											this.handlePlayerGameResult(gamePlayer, GameResult.WIN, resultLine, PlayerScore::increaseWin);
										} else {
											this.handlePlayerGameResult(gamePlayer, GameResult.LOSE, resultLine, PlayerScore::increaseLose);
										}
									}
								} else  {
									LOGGER.warn("Player {} finished the game but there is no result line", this.getName(player));
									this.stopGame();
								}
							} else if (this.winHandler.isDraw(map)) {
								for (Wins4ServerPlayer gamePlayer : this.players) {
									this.handlePlayerGameResult(gamePlayer, GameResult.DRAW, Wins4ResultLine.EMPTY, PlayerScore::increaseDraw);
								}
							} else {
								this.nextPlayer(false);
							}
						} else {
							LOGGER.warn("Fail to get unplaced figure for player {}, since all figures have been placed", this.getName(player));
							this.stopGame();
						}
					} else {
						LOGGER.warn("The field {} should be empty but there is a figure of player {} of it", fieldPos.getPosition(), this.getName(player));
						this.stopGame();
					}
				} else {
					LOGGER.warn("Fail to get empty field in column {}", fieldPos.getColumn());
					this.broadcastPlayer(new GameActionFailedPacket(), player);
				}
			} else {
				LOGGER.warn("Player {} tries to change the {} map at pos {} to {}, but it is not his turn", this.getName(player), fieldPos.getPosition(), player.getPlayerType());
			}
		}
	}
	
	protected void handlePlayerGameResult(Wins4ServerPlayer gamePlayer, GameResult result, Wins4ResultLine resultLine, Consumer<PlayerScore> consumer) {
		ServerPlayer player = gamePlayer.getPlayer();
		this.broadcastPlayer(new Wins4GameResultPacket(result, resultLine), gamePlayer);
		consumer.accept(player.getScore());
		this.broadcastPlayers(new SyncPlayerDataPacket(player.getProfile(), true, player.getScore()));
	}
	
	@Override
	public String toString() {
		return "Win4ServerGame";
	}
	
}
