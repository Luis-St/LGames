package net.vgc.server.games.ludo;

import com.google.common.collect.Lists;
import net.luis.utils.util.Utils;
import net.vgc.client.games.ludo.LudoClientGame;
import net.vgc.game.GameResult;
import net.vgc.game.dice.DiceHandler;
import net.vgc.game.map.field.GameField;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.figure.GameFigure;
import net.vgc.game.score.PlayerScore;
import net.vgc.game.type.GameType;
import net.vgc.game.type.GameTypes;
import net.vgc.games.ludo.player.LudoPlayerType;
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.client.SyncPlayerDataPacket;
import net.vgc.network.packet.client.game.CanSelectGameFieldPacket;
import net.vgc.network.packet.client.game.GameResultPacket;
import net.vgc.network.packet.client.game.UpdateGameMapPacket;
import net.vgc.network.packet.client.game.dice.CanRollDiceAgainPacket;
import net.vgc.network.packet.listener.PacketListener;
import net.vgc.network.packet.listener.PacketSubscriber;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.network.packet.server.game.SelectGameFieldPacket;
import net.vgc.server.Server;
import net.vgc.server.game.AbstractServerGame;
import net.vgc.server.games.ludo.dice.LudoDiceHandler;
import net.vgc.server.games.ludo.map.LudoServerMap;
import net.vgc.server.games.ludo.player.LudoServerPlayer;
import net.vgc.server.games.ludo.win.LudoWinHandler;
import net.vgc.server.player.ServerPlayer;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 *
 * @author Luis-st
 *
 */

@PacketSubscriber(value = NetworkSide.SERVER, getter = "#getGame")
public class LudoServerGame extends AbstractServerGame {
	
	private final LudoDiceHandler diceHandler;
	
	public LudoServerGame(Server server, List<ServerPlayer> players) {
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
	public void setPlayer(GamePlayer player) {
		player.setRollCount(player.hasAllFiguresAt(GameField::isHome) ? 3 : 1);
		super.setPlayer(player);
	}
	
	@Override
	public void nextPlayer(boolean random) {
		List<? extends GamePlayer> players = Lists.newArrayList(this.getPlayers());
		assert this.getWinHandler() != null;
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
						LOGGER.warn("Fail to get next player, since the player {} does not exists", player.getName());
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
	
	@PacketListener
	public void handlePacket(ServerPacket serverPacket) {
		if (serverPacket instanceof SelectGameFieldPacket packet) {
			GamePlayer player = this.getPlayerFor(packet.getProfile());
			assert player != null;
			if (Objects.equals(this.getPlayer(), player)) {
				int count = this.diceHandler.getLastCount(player);
				if (count != -1) {
					GameField currentField = this.getMap().getField(packet.getFieldType(), player.getPlayerType(), packet.getFieldPos());
					assert currentField != null;
					if (!currentField.isEmpty()) {
						GameFigure figure = currentField.getFigure();
						assert figure != null;
						GameField nextField = this.getMap().getNextField(figure, count);
						if (nextField != null) {
							if (this.getMap().moveFigureTo(figure, nextField)) {
								this.broadcastPlayers(new UpdateGameMapPacket(Utils.mapList(this.getMap().getFields(), GameField::getFieldInfo)));
								assert this.getWinHandler() != null;
								if (this.getWinHandler().hasPlayerFinished(player)) {
									this.getWinHandler().onPlayerFinished(player);
									if (this.getWinHandler().getWinOrder().size() - this.getPlayers().size() > 1) {
										this.nextPlayer(false);
									} else {
										LOGGER.info("Finished game {} with player win order: {}", this.getType().getInfoName(), Utils.mapList(this.getWinHandler().getWinOrder(), GamePlayer::getName));
										for (GamePlayer gamePlayer : this.getPlayers()) {
											PlayerScore score = gamePlayer.getPlayer().getScore();
											score.setScore(this.getWinHandler().getScoreFor(this, gamePlayer));
											this.broadcastPlayers(new SyncPlayerDataPacket(gamePlayer.getPlayer().getProfile(), true, score));
										}
										this.broadcastPlayers(new GameResultPacket(GameResult.NO, null));
									}
								} else if (this.diceHandler.canRollAfterMove(player, currentField, nextField, count)) {
									player.setRollCount(1);
									this.broadcastPlayer(new CanRollDiceAgainPacket(), player);
								} else {
									this.nextPlayer(false);
								}
							} else {
								LOGGER.warn("Fail to move figure {} of player {} to field {}", figure.getCount(), player.getName(), nextField.getFieldPos().getPosition());
								this.broadcastPlayer(new CanSelectGameFieldPacket(), player);
							}
						} else {
							LOGGER.warn("Fail to move figure {} of player {}, since there is no next field for the figure", figure.getCount(), player.getName());
							this.broadcastPlayer(new CanSelectGameFieldPacket(), player);
						}
					} else {
						LOGGER.warn("Fail to get a figure of player {} from field {}, since the field is empty", player.getName(), currentField.getFieldPos().getPosition());
						this.broadcastPlayer(new CanSelectGameFieldPacket(), player);
					}
				} else {
					LOGGER.warn("Fail to move figure of player {}, since the player has not rolled the dice yet", player.getName());
					this.stop();
				}
			} else {
				LOGGER.warn("Player {} tries to change the map at pos {} to {}, but it is not his turn", player.getName(), packet.getFieldPos().getPosition(), player.getPlayerType());
			}
		}
	}
	
	@Override
	public String toString() {
		return "LudoServerGame";
	}
	
}
