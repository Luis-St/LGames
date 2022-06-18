package net.vgc.server.game.games.ludo;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.vgc.client.game.games.ludo.LudoClientGame;
import net.vgc.game.GameType;
import net.vgc.game.GameTypes;
import net.vgc.game.games.ludo.map.field.LudoFieldPos;
import net.vgc.game.games.ludo.map.field.LudoFieldType;
import net.vgc.game.games.ludo.player.LudoPlayerType;
import net.vgc.game.map.field.GameField;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.score.PlayerScore;
import net.vgc.network.packet.client.SyncPlayerDataPacket;
import net.vgc.network.packet.client.game.CurrentPlayerUpdatePacket;
import net.vgc.network.packet.client.game.GameActionFailedPacket;
import net.vgc.network.packet.client.game.LudoGameResultPacket;
import net.vgc.network.packet.client.game.UpdateGameMapPacket;
import net.vgc.network.packet.client.game.dice.CanRollDiceAgainPacket;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.network.packet.server.game.SelectGameFieldPacket;
import net.vgc.player.GameProfile;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.game.ServerGame;
import net.vgc.server.game.dice.DiceHandler;
import net.vgc.server.game.games.ludo.dice.LudoDiceHandler;
import net.vgc.server.game.games.ludo.map.LudoServerMap;
import net.vgc.server.game.games.ludo.map.field.LudoServerField;
import net.vgc.server.game.games.ludo.player.LudoServerPlayer;
import net.vgc.server.game.games.ludo.player.figure.LudoServerFigure;
import net.vgc.server.game.games.ludo.win.LudoWinHandler;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Mth;
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
		this.players = createGamePlayers(this, players, 1);
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
			this.player.setRollCount(this.getRollCount());
			this.server.getPlayerList().broadcastAll(Util.mapList(this.players, LudoServerPlayer::getPlayer), new CurrentPlayerUpdatePacket(this.player));
		}
	}
	
	@Override
	public void nextPlayer(boolean random) {
		List<? extends GamePlayer> players = Lists.newArrayList(this.players);
		players.removeIf(this.winHandler.getWinOrder()::contains);
		if (!players.isEmpty()) {
			if (random) {
				this.setCurrentPlayer(players.get(new Random().nextInt(players.size())));
			} else {
				GamePlayer player = this.getCurrentPlayer();
				if (player == null) {
					this.setCurrentPlayer(players.get(0));
				} else {
					int index = players.indexOf(player);
					if (index != -1) {
						index++;
						if (index >= players.size()) {
							this.setCurrentPlayer(players.get(0));
						} else {
							this.setCurrentPlayer(players.get(index));
						}
					} else {
						LOGGER.warn("Fail to get next player, since the player {} does not exists", this.getName(player));
						this.setCurrentPlayer(players.get(0));
					}
				}
			}
		} else {
			LOGGER.warn("Unable to change player, since there is no player present");
		}
	}
	
	protected int getRollCount() {
		if (this.player.hasAllFiguresAt(GameField::isHome)) {
			return 3;
		}
		return 1;
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
		if (Mth.isInBounds(this.players.size(), this.getType().getMinPlayers(), this.getType().getMaxPlayers())) {
			this.map.reset();
			this.map.init(this.players);
			this.diceHandler.reset();
			this.winHandler.reset();
			this.nextPlayer(true);
			this.broadcastPlayers(new UpdateGameMapPacket(Util.mapList(this.getMap().getFields(), LudoServerField::getFieldInfo)));
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
			LudoFieldPos fieldPos = (LudoFieldPos) packet.getFieldPos();
			LudoFieldType fieldType = (LudoFieldType) packet.getFieldType();
			LudoServerPlayer player = (LudoServerPlayer) this.getPlayerFor(packet.getProfile());
			if (Objects.equals(this.player, player)) {
				int count = this.diceHandler.getLastCount(player);
				if (count != -1) {
					LudoServerField currentField = this.map.getField(fieldType, player.getPlayerType(), fieldPos);
					LudoServerFigure figure = currentField.getFigure();
					LudoServerField nextField = this.map.getNextField(figure, count);
					if (nextField != null) {
						if (this.map.moveFigureTo(figure, nextField)) {
							this.broadcastPlayers(new UpdateGameMapPacket(Util.mapList(this.getMap().getFields(), LudoServerField::getFieldInfo)));
							if (this.winHandler.hasPlayerFinished(player)) {
								this.winHandler.onPlayerFinished(player);
								if (this.winHandler.getWinOrder().size() - this.players.size() > 1) {
									this.nextPlayer(false);
								} else {
									LOGGER.info("Finished game {} with player win order: {}", this.getType().getInfoName(), Util.mapList(this.winHandler.getWinOrder(), this::getName));
									for (LudoServerPlayer gamePlayer : this.players) {
										PlayerScore score = gamePlayer.getPlayer().getScore();
										score.setScore(this.winHandler.getScoreFor(this, gamePlayer));
										this.broadcastPlayers(new SyncPlayerDataPacket(gamePlayer.getPlayer().getProfile(), true, score));
									}
									this.broadcastPlayers(new LudoGameResultPacket());
								}
							} else if (this.diceHandler.canRollAfterMove(player, currentField, nextField, count)) {
								player.setRollCount(1);
								this.broadcastPlayer(new CanRollDiceAgainPacket(), player);
							} else {
								this.nextPlayer(false);
							}
						} else {
							LOGGER.warn("Fail to move figure {} of player {} to field {}", figure.getCount(), this.getName(player), nextField.getFieldPos().getPosition());
							this.broadcastPlayer(new GameActionFailedPacket(), player);
						}
					} else {
						LOGGER.warn("Fail to move figure {} of player {}, since there is no next field for the figure", figure.getCount(), this.getName(player));
						this.broadcastPlayer(new GameActionFailedPacket(), player);
					}
				} else {
					LOGGER.warn("Fail to move figure of player {}, since the player has not rolled the dice yet", this.getName(player));
					this.stopGame();
				}
			} else {
				LOGGER.warn("Player {} tries to change the {} map at pos {} to {}, but it is not his turn", this.getName(player), fieldPos.getPosition(), player.getPlayerType());
			}
		}
	}
	
	@Override
	public String toString() {
		return "LudoServerGame";
	}
	
}
