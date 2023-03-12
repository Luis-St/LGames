package net.luis.ttt;

import net.luis.game.AbstractGame;
import net.luis.game.GameResult;
import net.luis.game.map.field.GameField;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.*;
import net.luis.game.player.game.GamePlayer;
import net.luis.game.player.game.GamePlayerFactory;
import net.luis.game.player.game.GamePlayerInfo;
import net.luis.game.player.game.figure.GameFigure;
import net.luis.game.player.score.PlayerScore;
import net.luis.game.type.GameType;
import net.luis.game.type.GameTypes;
import net.luis.game.win.GameResultLine;
import net.luis.network.packet.client.SyncPlayerDataPacket;
import net.luis.network.packet.client.game.GameActionFailedPacket;
import net.luis.network.packet.client.game.GameResultPacket;
import net.luis.network.packet.client.game.UpdateGameMapPacket;
import net.luis.network.listener.PacketListener;
import net.luis.network.packet.server.ServerPacket;
import net.luis.network.packet.server.game.SelectGameFieldPacket;
import net.luis.ttt.map.TTTMap;
import net.luis.ttt.win.TTTWinHandler;
import net.luis.utils.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 *
 * @author Luis-st
 *
 */

public class TTTGame extends AbstractGame {
	
	public TTTGame(List<GamePlayerInfo> playerInfos, GamePlayerFactory playerFactory) {
		super(TTTMap::new, playerInfos, playerFactory, new TTTWinHandler());
	}
	
	@Override
	public @NotNull GameType<?> getType() {
		return GameTypes.TIC_TAC_TOE;
	}
	
	@PacketListener(ServerPacket.class)
	public void handlePacket(ServerPacket serverPacket) {
		if (serverPacket instanceof SelectGameFieldPacket packet) {
			GamePlayer player = this.getPlayerFor(packet.getProfile().cast(GameProfile.class));
			assert player != null;
			GameFieldPos fieldPos = packet.getFieldPos().cast(GameFieldPos.class);
			if (Objects.equals(this.getPlayer(), player)) {
				GameField field = this.getMap().getField(null, null, fieldPos);
				if (field != null) {
					if (field.isEmpty()) {
						GameFigure figure = player.getFigure((map, gameFigure) -> map.getField(gameFigure) == null);
						if (figure != null) {
							field.setFigure(figure);
							this.broadcastPlayers(new UpdateGameMapPacket(Utils.mapList(this.getMap().getFields(), GameField::getFieldInfo)));
							assert this.getWinHandler() != null;
							if (this.getWinHandler().hasPlayerFinished(player)) {
								this.getWinHandler().onPlayerFinished(player);
								LOGGER.info("Finished game {} with player win order: {}", this.getType().getInfoName(), Utils.mapList(this.getWinHandler().getWinOrder(), GamePlayer::getName));
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
						LOGGER.warn("Fail to place a figure of player {} on field, since on the field is already a figure of type {}", player.getName(), Objects.requireNonNull(field.getFigure()).getPlayerType());
						this.broadcastPlayer(new GameActionFailedPacket(), player);
					}
				} else {
					LOGGER.warn("Fail to get field for pos {}", fieldPos.getPosition());
					this.broadcastPlayer(new GameActionFailedPacket(), player);
				}
			} else {
				LOGGER.warn("Player {} tries to change the map at pos {} to {}, but it is not his turn", player.getName(), fieldPos.getPosition(), player.getPlayerType());
			}
		}
	}
	
	private void handlePlayerGameResult(GamePlayer gamePlayer, GameResult result, GameResultLine resultLine, Consumer<PlayerScore> consumer) {
		Player player = gamePlayer.getPlayer();
		this.broadcastPlayer(new GameResultPacket(result, resultLine), gamePlayer);
		consumer.accept(player.getScore());
		this.broadcastPlayers(new SyncPlayerDataPacket(player.getProfile(), true, player.getScore()));
	}
	
}
