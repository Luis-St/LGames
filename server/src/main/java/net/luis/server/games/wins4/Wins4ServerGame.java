package net.luis.server.games.wins4;

import com.google.common.collect.Lists;
import net.luis.client.games.wins4.Wins4ClientGame;
import net.luis.game.GameResult;
import net.luis.game.map.field.GameField;
import net.luis.game.player.GamePlayer;
import net.luis.game.player.figure.GameFigure;
import net.luis.game.score.PlayerScore;
import net.luis.game.type.GameType;
import net.luis.game.type.GameTypes;
import net.luis.game.win.GameResultLine;
import net.luis.games.wins4.map.field.Wins4FieldPos;
import net.luis.games.wins4.map.field.Wins4FieldType;
import net.luis.games.wins4.player.Wins4PlayerType;
import net.luis.network.packet.client.SyncPlayerDataPacket;
import net.luis.network.packet.client.game.GameActionFailedPacket;
import net.luis.network.packet.client.game.GameResultPacket;
import net.luis.network.packet.client.game.UpdateGameMapPacket;
import net.luis.network.packet.listener.PacketListener;
import net.luis.network.packet.listener.PacketSubscriber;
import net.luis.network.packet.server.ServerPacket;
import net.luis.network.packet.server.game.SelectGameFieldPacket;
import net.luis.player.Player;
import net.luis.server.Server;
import net.luis.server.game.AbstractServerGame;
import net.luis.server.games.wins4.map.Wins4ServerMap;
import net.luis.server.games.wins4.player.Wins4ServerPlayer;
import net.luis.server.games.wins4.win.Wins4WinHandler;
import net.luis.server.player.ServerPlayer;
import net.luis.utils.math.Mth;
import net.luis.utils.util.Utils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 *
 * @author Luis-st
 *
 */

@PacketSubscriber("#getGame")
public class Wins4ServerGame extends AbstractServerGame {
	
	public Wins4ServerGame(Server server, List<ServerPlayer> players) {
		super(server, Wins4ServerMap::new, players, Wins4PlayerType.values(), Wins4ServerPlayer::new, new Wins4WinHandler());
	}
	
	@Override
	public GameType<Wins4ServerGame, Wins4ClientGame> getType() {
		return GameTypes.WINS_4;
	}
	
	@PacketListener
	public void handlePacket(ServerPacket serverPacket) {
		if (serverPacket instanceof SelectGameFieldPacket packet) {
			GamePlayer player = this.getPlayerFor(packet.getProfile());
			assert player != null;
			if (Objects.equals(this.getPlayer(), player)) {
				Optional<GameField> optionalField = Utils.reverseList(this.getFieldsForColumn(packet.getFieldPos().getColumn())).stream().filter(GameField::isEmpty).findFirst();
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
								Game.LOGGER.info("Finished game {} with player win order: {}", this.getType().getInfoName(), Utils.mapList(this.getWinHandler().getWinOrder(), GamePlayer::getName));
								GameResultLine resultLine = this.getWinHandler().getResultLine(this.getMap());
								Game.LOGGER.debug("Result line of player {} is {}", player.getName(), resultLine);
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
						Game.LOGGER.warn("The field {} should be empty but there is a figure of player {} of it", packet.getFieldPos().getPosition(), player.getName());
						this.stop();
					}
				} else {
					Game.LOGGER.warn("Fail to get empty field in column {}", packet.getFieldPos().getColumn());
					this.broadcastPlayer(new GameActionFailedPacket(), player);
				}
			} else {
				Game.LOGGER.warn("Player {} tries to change the map at pos {} to {}, but it is not his turn", player.getName(), packet.getFieldPos().getPosition(), player.getPlayerType());
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
	
	@Override
	public String toString() {
		return "Win4ServerGame";
	}
	
}
