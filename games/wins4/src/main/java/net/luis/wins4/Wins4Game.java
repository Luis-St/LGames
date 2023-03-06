package net.luis.wins4;

import com.google.common.collect.Lists;
import net.luis.game.AbstractGame;
import net.luis.game.GameResult;
import net.luis.game.map.field.GameField;
import net.luis.game.player.*;
import net.luis.game.player.figure.GameFigure;
import net.luis.game.player.score.PlayerScore;
import net.luis.game.type.GameType;
import net.luis.game.type.GameTypes;
import net.luis.game.win.GameResultLine;
import net.luis.network.packet.client.SyncPlayerDataPacket;
import net.luis.network.packet.client.game.GameActionFailedPacket;
import net.luis.network.packet.client.game.GameResultPacket;
import net.luis.network.packet.client.game.UpdateGameMapPacket;
import net.luis.network.packet.listener.PacketListener;
import net.luis.network.packet.server.ServerPacket;
import net.luis.network.packet.server.game.SelectGameFieldPacket;
import net.luis.utils.math.Mth;
import net.luis.utils.util.Utils;
import net.luis.wins4.map.Wins4Map;
import net.luis.wins4.map.field.Wins4FieldPos;
import net.luis.wins4.map.field.Wins4FieldType;
import net.luis.wins4.win.Wins4WinHandler;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 *
 * @author Luis-st
 *
 */

public class Wins4Game extends AbstractGame {
	
	protected Wins4Game(List<GamePlayerInfo> playerInfos, GamePlayerFactory playerFactory) {
		super(Wins4Map::new, playerInfos, playerFactory, new Wins4WinHandler());
	}
	
	@Override
	public GameType<?> getType() {
		return GameTypes.WINS_4;
	}
	
	@PacketListener(ServerPacket.class)
	public void handlePacket(ServerPacket serverPacket) {
		if (serverPacket instanceof SelectGameFieldPacket packet) {
			GamePlayer player = this.getPlayerFor(packet.getProfile().cast(GameProfile.class));
			assert player != null;
			Wins4FieldPos fieldPos = packet.getFieldPos().cast(Wins4FieldPos.class);
			if (Objects.equals(this.getPlayer(), player)) {
				Optional<GameField> optionalField = Utils.reverseList(this.getFieldsForColumn(fieldPos.getColumn())).stream().filter(GameField::isEmpty).findFirst();
				if (optionalField.isPresent()) {
					GameField field = optionalField.orElseThrow(NullPointerException::new);
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
								LOGGER.debug("Result line of player {} is {}", player.getName(), resultLine);
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
						LOGGER.warn("The field {} should be empty but there is a figure of player {} of it", fieldPos.getPosition(), player.getName());
						this.stop();
					}
				} else {
					LOGGER.warn("Fail to get empty field in column {}", fieldPos.getColumn());
					this.broadcastPlayer(new GameActionFailedPacket(), player);
				}
			} else {
				LOGGER.warn("Player {} tries to change the map at pos {} to {}, but it is not his turn", player.getName(), fieldPos.getPosition(), player.getPlayerType());
			}
		}
	}
	
	private List<GameField> getFieldsForColumn(int column) {
		if (Mth.isInBounds(column, 0, 6)) {
			List<GameField> fields = Lists.newArrayList();
			for (int i = 0; i < 6; i++) {
				fields.add(this.getMap().getField(Wins4FieldType.DEFAULT, null, Wins4FieldPos.of(i, column)));
			}
			return fields;
		}
		return Lists.newArrayList();
	}
	
	private void handlePlayerGameResult(GamePlayer gamePlayer, GameResult result, GameResultLine resultLine, Consumer<PlayerScore> consumer) {
		Player player = gamePlayer.getPlayer();
		this.broadcastPlayer(new GameResultPacket(result, resultLine), gamePlayer);
		consumer.accept(player.getScore());
		this.broadcastPlayers(new SyncPlayerDataPacket(player.getProfile(), true, player.getScore()));
	}
	
}