package net.vgc.server.games.wins4;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.vgc.client.games.wins4.Wins4ClientGame;
import net.vgc.game.GameResult;
import net.vgc.game.map.field.GameField;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.score.PlayerScore;
import net.vgc.game.type.GameType;
import net.vgc.game.type.GameTypes;
import net.vgc.game.win.GameResultLine;
import net.vgc.games.wins4.map.field.Wins4FieldPos;
import net.vgc.games.wins4.player.Wins4PlayerType;
import net.vgc.network.packet.client.SyncPlayerDataPacket;
import net.vgc.network.packet.client.game.GameActionFailedPacket;
import net.vgc.network.packet.client.game.UpdateGameMapPacket;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.network.packet.server.game.SelectGameFieldPacket;
import net.vgc.player.Player;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.game.AbstractServerGame;
import net.vgc.server.games.ttt.player.TTTServerPlayer;
import net.vgc.server.games.wins4.map.Wins4ServerMap;
import net.vgc.server.games.wins4.map.field.Wins4ServerField;
import net.vgc.server.games.wins4.player.Wins4ServerPlayer;
import net.vgc.server.games.wins4.player.figure.Wins4ServerFigure;
import net.vgc.server.games.wins4.win.Wins4WinHandler;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Util;

/**
 *
 * @author Luis-st
 *
 */

public class Wins4ServerGame extends AbstractServerGame {
	
	public Wins4ServerGame(DedicatedServer server, List<ServerPlayer> players) {
		super(server, Wins4ServerMap::new, players, Wins4PlayerType.values(), TTTServerPlayer::new, new Wins4WinHandler());
	}
	
	@Override
	public GameType<Wins4ServerGame, Wins4ClientGame> getType() {
		return GameTypes.WINS_4;
	}
	
	@Override
	public void handlePacket(ServerPacket serverPacket) {
		if (serverPacket instanceof SelectGameFieldPacket packet) {
			GamePlayer player = this.getPlayerFor(packet.getProfile());
			if (Objects.equals(this.getPlayer(), player)) {
				Optional<Wins4ServerField> optionalField = Util.reverseList(this.getMap().getFieldsForColumn(packet.getFieldPos().getColumn())).stream().filter(Wins4ServerField::isEmpty).findFirst();
				if (optionalField.isPresent()) {
					Wins4ServerField field = optionalField.orElseThrow(NullPointerException::new);
					if (field.isEmpty()) {
						Wins4ServerFigure figure = player.getUnplacedFigure();
						if (figure != null) {
							field.setFigure(figure);
							this.broadcastPlayers(new UpdateGameMapPacket(Util.mapList(this.getMap().getFields(), GameField::getFieldInfo)));
							if (this.getWinHandler().hasPlayerFinished(player)) {
								this.getWinHandler().onPlayerFinished(player);
								LOGGER.info("Finished game {} with player win order: {}", this.getType().getInfoName(), Util.mapList(this.getWinHandler().getWinOrder(), GamePlayer::getName));
								GameResultLine resultLine = this.getWinHandler().getResultLine(this.getMap());
								LOGGER.debug("Result line of player {} is {}", player.getName(), resultLine);
								if (resultLine != GameResultLine.EMPTY) {
									for (GamePlayer gamePlayer : this.getPlayers()) {
										if (gamePlayer.equals(player))  {
											this.handlePlayerGameResult(gamePlayer, GameResult.WIN, resultLine, PlayerScore::increaseWin);
										} else {
											this.handlePlayerGameResult(gamePlayer, GameResult.LOSE, resultLine, PlayerScore::increaseLose);
										}
									}
								} else  {
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
						LOGGER.warn("The field {} should be empty but there is a figure of player {} of it", packet.getFieldPos().getPosition(), player.getName());
						this.stop();
					}
				} else {
					LOGGER.warn("Fail to get empty field in column {}", packet.getFieldPos().getColumn());
					this.broadcastPlayer(new GameActionFailedPacket(), player);
				}
			} else {
				LOGGER.warn("Player {} tries to change the {} map at pos {} to {}, but it is not his turn", player.getName(), packet.getFieldPos().getPosition(), player.getPlayerType());
			}
		}
	}
	
	private void handlePlayerGameResult(Wins4ServerPlayer gamePlayer, GameResult result, GameResultLine resultLine, Consumer<PlayerScore> consumer) {
		Player player = gamePlayer.getPlayer();
		this.broadcastPlayer(new Wins4GameResultPacket(result, resultLine), gamePlayer);
		consumer.accept(player.getScore());
		this.broadcastPlayers(new SyncPlayerDataPacket(player.getProfile(), true, player.getScore()));
	}
	
	@Override
	public String toString() {
		return "Win4ServerGame";
	}
	
}
