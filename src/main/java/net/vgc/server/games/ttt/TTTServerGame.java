package net.vgc.server.games.ttt;

import net.vgc.client.games.ttt.TTTClientGame;
import net.vgc.game.GameResult;
import net.vgc.game.map.field.GameField;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.figure.GameFigure;
import net.vgc.game.score.PlayerScore;
import net.vgc.game.type.GameType;
import net.vgc.game.type.GameTypes;
import net.vgc.game.win.GameResultLine;
import net.vgc.games.ttt.player.TTTPlayerType;
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.client.SyncPlayerDataPacket;
import net.vgc.network.packet.client.game.GameActionFailedPacket;
import net.vgc.network.packet.client.game.GameResultPacket;
import net.vgc.network.packet.client.game.UpdateGameMapPacket;
import net.vgc.network.packet.listener.PacketListener;
import net.vgc.network.packet.listener.PacketSubscriber;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.network.packet.server.game.SelectGameFieldPacket;
import net.vgc.player.Player;
import net.vgc.server.Server;
import net.vgc.server.game.AbstractServerGame;
import net.vgc.server.games.ttt.map.TTTServerMap;
import net.vgc.server.games.ttt.player.TTTServerPlayer;
import net.vgc.server.games.ttt.win.TTTWinHandler;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Util;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 *
 * @author Luis-st
 *
 */

@PacketSubscriber(value = NetworkSide.SERVER, getter = "#getGame")
public class TTTServerGame extends AbstractServerGame {
	
	public TTTServerGame(Server server, List<ServerPlayer> players) {
		super(server, TTTServerMap::new, players, TTTPlayerType.values(), TTTServerPlayer::new, new TTTWinHandler());
	}
	
	@Override
	public GameType<TTTServerGame, TTTClientGame> getType() {
		return GameTypes.TIC_TAC_TOE;
	}
	
	@PacketListener
	public void handlePacket(ServerPacket serverPacket) {
		if (serverPacket instanceof SelectGameFieldPacket packet) {
			GamePlayer player = this.getPlayerFor(packet.getProfile());
			if (Objects.equals(this.getPlayer(), player)) {
				GameField field = this.getMap().getField(null, null, packet.getFieldPos());
				if (field != null) {
					if (field.isEmpty()) {
						GameFigure figure = player.getFigure((map, gameFigure) -> {
							return map.getField(gameFigure) == null;
						});
						if (figure != null) {
							field.setFigure(figure);
							this.broadcastPlayers(new UpdateGameMapPacket(Util.mapList(this.getMap().getFields(), GameField::getFieldInfo)));
							if (this.getWinHandler().hasPlayerFinished(player)) {
								this.getWinHandler().onPlayerFinished(player);
								LOGGER.info("Finished game {} with player win order: {}", this.getType().getInfoName(), Util.mapList(this.getWinHandler().getWinOrder(), GamePlayer::getName));
								GameResultLine resultLine = this.getWinHandler().getResultLine(this.getMap());
								if (resultLine != GameResultLine.EMPTY) {
									for (GamePlayer gamePlayer : this.getPlayers()) {
										if (gamePlayer.equals(player)) {
											this.handlePlayerGameResult(gamePlayer, GameResult.WIN, resultLine, PlayerScore::increaseWin);
										} else {
											this.handlePlayerGameResult(gamePlayer, GameResult.LOSE, resultLine, PlayerScore::increaseLose);
										}
									}
								} else {
									LOGGER.warn("Player {} finished the game but there is no result line", player.getName());
									this.stop();
								}
							} else if (this.getWinHandler().isDraw(this.getMap())) {
								for (GamePlayer gamePlayer : this.getPlayers()) {
									this.handlePlayerGameResult(gamePlayer, GameResult.DRAW, GameResultLine.EMPTY, PlayerScore::increaseDraw);
								}
							} else {
								this.nextPlayer(false);
							}
						} else {
							LOGGER.warn("Fail to get unplaced figure for player {}, since all figures have been placed", player.getName());
							this.stop();
						}
					} else {
						LOGGER.warn("Fail to place a figure of player {} on field, since on the field is already a figure of type {}", player.getName(), field.getFigure().getPlayerType());
						this.broadcastPlayer(new GameActionFailedPacket(), player);
					}
				} else {
					LOGGER.warn("Fail to get field for pos {}", packet.getFieldPos().getPosition());
					this.broadcastPlayer(new GameActionFailedPacket(), player);
				}
			} else {
				LOGGER.warn("Player {} tries to change the {} map at pos {} to {}, but it is not his turn", player.getName(), packet.getFieldPos().getPosition(), player.getPlayerType());
			}
		}
	}
	
	private void handlePlayerGameResult(GamePlayer gamePlayer, GameResult result, GameResultLine resultLine, Consumer<PlayerScore> consumer) {
		Player player = gamePlayer.getPlayer();
		this.broadcastPlayer(new GameResultPacket(result, resultLine), gamePlayer);
		consumer.accept(player.getScore());
		this.broadcastPlayers(new SyncPlayerDataPacket(player.getProfile(), true, player.getScore()));
	}
	
	@Override
	public String toString() {
		return "TTTServerGame";
	}
	
}
