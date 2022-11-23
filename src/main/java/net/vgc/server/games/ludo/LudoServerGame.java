package net.vgc.server.games.ludo;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.vgc.client.games.ludo.LudoClientGame;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.type.GameType;
import net.vgc.game.type.GameTypes;
import net.vgc.games.ludo.player.LudoPlayerType;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.game.AbstractServerGame;
import net.vgc.server.game.dice.DiceHandler;
import net.vgc.server.games.ludo.dice.LudoDiceHandler;
import net.vgc.server.games.ludo.map.LudoServerMap;
import net.vgc.server.games.ludo.player.LudoServerPlayer;
import net.vgc.server.games.ludo.win.LudoWinHandler;
import net.vgc.server.player.ServerPlayer;

public class LudoServerGame extends AbstractServerGame {
	
	protected final LudoDiceHandler diceHandler;
	
	public LudoServerGame(DedicatedServer server, List<ServerPlayer> players) {
		super(server, LudoServerMap::new, players, LudoPlayerType.values(), (game, player, playerType) -> {
			return new LudoServerPlayer(game, player, playerType, 4);
		}, new LudoWinHandler());
		this.diceHandler = new LudoDiceHandler(this, 1, 6);
	}
	
	@Override
	public GameType<LudoServerGame, LudoClientGame> getType() {
		return GameTypes.LUDO;
	}
	
	@Override
	public void nextPlayer(boolean random) {
		List<? extends GamePlayer> players = Lists.newArrayList(this.getPlayers());
		players.removeIf(this.getWinHandler().getWinOrder()::contains);
		if (!players.isEmpty()) {
			if (random) {
				this.setPlayer(players.get(new Random().nextInt(players.size())));
			} else {
				GamePlayer player = this.getPlayer();
				if (player == null) {
					this.setPlayer(players.get(0));
				} else {
					int index = players.indexOf(player);
					if (index != -1) {
						index++;
						if (index >= players.size()) {
							this.setPlayer(players.get(0));
						} else {
							this.setPlayer(players.get(index));
						}
					} else {
						LOGGER.warn("Fail to get next player, since the player {} does not exists", this.getName(player));
						this.setPlayer(players.get(0));
					}
				}
			}
		} else {
			LOGGER.warn("Unable to change player, since there is no player present");
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
	/*@Override
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
					if (!currentField.isEmpty()) {
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
								this.broadcastPlayer(new CanSelectGameFieldPacket(), player);
							}
						} else {
							LOGGER.warn("Fail to move figure {} of player {}, since there is no next field for the figure", figure.getCount(), this.getName(player));
							this.broadcastPlayer(new CanSelectGameFieldPacket(), player);
						}
					} else {
						LOGGER.warn("Fail to get a figure of player {} from field {}, since the field is empty", this.getName(player), currentField.getFieldPos().getPosition());
						this.broadcastPlayer(new CanSelectGameFieldPacket(), player);
					}
				} else {
					LOGGER.warn("Fail to move figure of player {}, since the player has not rolled the dice yet", this.getName(player));
					this.stopGame();
				}
			} else {
				LOGGER.warn("Player {} tries to change the {} map at pos {} to {}, but it is not his turn", this.getName(player), fieldPos.getPosition(), player.getPlayerType());
			}
		}
	}*/
	
	@Override
	public String toString() {
		return "LudoServerGame";
	}
	
}
