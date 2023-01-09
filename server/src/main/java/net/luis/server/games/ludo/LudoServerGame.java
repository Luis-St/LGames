package net.luis.server.games.ludo;

import com.google.common.collect.Lists;
import net.luis.client.games.ludo.LudoClientGame;
import net.luis.game.GameResult;
import net.luis.game.dice.DiceHandler;
import net.luis.game.map.field.GameField;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.figure.GameFigure;
import net.luis.game.score.PlayerScore;
import net.luis.game.type.GameType;
import net.luis.game.type.GameTypes;
import net.luis.games.ludo.player.LudoPlayerType;
import net.luis.network.packet.client.SyncPlayerDataPacket;
import net.luis.network.packet.client.game.CanSelectGameFieldPacket;
import net.luis.network.packet.client.game.GameResultPacket;
import net.luis.network.packet.client.game.UpdateGameMapPacket;
import net.luis.network.packet.client.game.dice.CanRollDiceAgainPacket;
import net.luis.network.packet.listener.PacketListener;
import net.luis.network.packet.listener.PacketSubscriber;
import net.luis.network.packet.server.ServerPacket;
import net.luis.network.packet.server.game.SelectGameFieldPacket;
import net.luis.server.Server;
import net.luis.server.game.AbstractServerGame;
import net.luis.server.games.ludo.dice.LudoDiceHandler;
import net.luis.server.games.ludo.map.LudoServerMap;
import net.luis.server.games.ludo.player.LudoServerPlayer;
import net.luis.server.games.ludo.win.LudoWinHandler;
import net.luis.server.player.ServerPlayer;
import net.luis.utils.util.Utils;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 *
 * @author Luis-st
 *
 */

@PacketSubscriber("#getGame")
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
						Game.LOGGER.warn("Fail to get next player, since the player {} does not exists", player.getName());
						this.setPlayer(players.get(0));
					}
				}
			}
		} else {
			Game.LOGGER.warn("Unable to change player, since there is no player present");
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
										Game.LOGGER.info("Finished game {} with player win order: {}", this.getType().getInfoName(), Utils.mapList(this.getWinHandler().getWinOrder(), GamePlayer::getName));
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
								Game.LOGGER.warn("Fail to move figure {} of player {} to field {}", figure.getCount(), player.getName(), nextField.getFieldPos().getPosition());
								this.broadcastPlayer(new CanSelectGameFieldPacket(), player);
							}
						} else {
							Game.LOGGER.warn("Fail to move figure {} of player {}, since there is no next field for the figure", figure.getCount(), player.getName());
							this.broadcastPlayer(new CanSelectGameFieldPacket(), player);
						}
					} else {
						Game.LOGGER.warn("Fail to get a figure of player {} from field {}, since the field is empty", player.getName(), currentField.getFieldPos().getPosition());
						this.broadcastPlayer(new CanSelectGameFieldPacket(), player);
					}
				} else {
					Game.LOGGER.warn("Fail to move figure of player {}, since the player has not rolled the dice yet", player.getName());
					this.stop();
				}
			} else {
				Game.LOGGER.warn("Player {} tries to change the map at pos {} to {}, but it is not his turn", player.getName(), packet.getFieldPos().getPosition(), player.getPlayerType());
			}
		}
	}
	
	@Override
	public String toString() {
		return "LudoServerGame";
	}
	
}
