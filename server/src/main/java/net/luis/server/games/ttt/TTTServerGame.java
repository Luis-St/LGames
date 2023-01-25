package net.luis.server.games.ttt;

import net.luis.client.games.ttt.TTTClientGame;
import net.luis.game.GameResult;
import net.luis.game.map.field.GameField;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.Player;
import net.luis.game.player.figure.GameFigure;
import net.luis.game.player.score.PlayerScore;
import net.luis.game.type.GameType;
import net.luis.game.type.GameTypes;
import net.luis.game.win.GameResultLine;
import net.luis.games.ttt.player.TTTPlayerType;
import net.luis.network.packet.client.SyncPlayerDataPacket;
import net.luis.network.packet.client.game.GameActionFailedPacket;
import net.luis.network.packet.client.game.GameResultPacket;
import net.luis.network.packet.client.game.UpdateGameMapPacket;
import net.luis.network.packet.listener.PacketListener;
import net.luis.network.packet.listener.PacketSubscriber;
import net.luis.network.packet.server.ServerPacket;
import net.luis.network.packet.server.game.SelectGameFieldPacket;
import net.luis.server.Server;
import net.luis.server.game.AbstractServerGame;
import net.luis.server.games.ttt.map.TTTServerMap;
import net.luis.server.games.ttt.player.TTTServerPlayer;
import net.luis.server.games.ttt.win.TTTWinHandler;
import net.luis.server.player.ServerPlayer;
import net.luis.utils.util.Utils;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 *
 * @author Luis-st
 *
 */

@PacketSubscriber("#getGame")
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
			assert player != null;
			if (Objects.equals(this.getPlayer(), player)) {
				GameField field = this.getMap().getField(null, null, packet.getFieldPos());
				if (field != null) {
					if (field.isEmpty()) {
						GameFigure figure = player.getFigure((map, gameFigure) -> map.getField(gameFigure) == null);
						if (figure != null) {
							field.setFigure(figure);
							this.broadcastPlayers(new UpdateGameMapPacket(Utils.mapList(this.getMap().getFields(), GameField::getFieldInfo)));
							assert this.getWinHandler() != null;
							if (this.getWinHandler().hasPlayerFinished(player)) {
								this.getWinHandler().onPlayerFinished(player);
								Game.LOGGER.info("Finished game {} with player win order: {}", this.getType().getInfoName(), Utils.mapList(this.getWinHandler().getWinOrder(), GamePlayer::getName));
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
									Game.LOGGER.warn("Player {} finished the game but there is no result line", player.getName());
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
							Game.LOGGER.warn("Fail to get unplaced figure for player {}, since all figures have been placed", player.getName());
							this.stop();
						}
					} else {
						Game.LOGGER.warn("Fail to place a figure of player {} on field, since on the field is already a figure of type {}", player.getName(), Objects.requireNonNull(field.getFigure()).getPlayerType());
						this.broadcastPlayer(new GameActionFailedPacket(), player);
					}
				} else {
					Game.LOGGER.warn("Fail to get field for pos {}", packet.getFieldPos().getPosition());
					this.broadcastPlayer(new GameActionFailedPacket(), player);
				}
			} else {
				Game.LOGGER.warn("Player {} tries to change the map at pos {} to {}, but it is not his turn", player.getName(), packet.getFieldPos().getPosition(), player.getPlayerType());
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
