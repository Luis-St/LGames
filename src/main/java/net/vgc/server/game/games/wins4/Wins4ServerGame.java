package net.vgc.server.game.games.wins4;

import java.util.List;

import net.vgc.client.game.games.wins4.Wins4ClientGame;
import net.vgc.game.GameType;
import net.vgc.game.GameTypes;
import net.vgc.game.games.wins4.player.Wins4PlayerType;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.game.AbstractServerGame;
import net.vgc.server.game.games.ttt.player.TTTServerPlayer;
import net.vgc.server.game.games.wins4.map.Wins4ServerMap;
import net.vgc.server.game.games.wins4.win.Wins4WinHandler;
import net.vgc.server.player.ServerPlayer;

public class Wins4ServerGame extends AbstractServerGame {
	
	public Wins4ServerGame(DedicatedServer server, List<ServerPlayer> players) {
		super(server, Wins4ServerMap::new, players, Wins4PlayerType.values(), TTTServerPlayer::new, new Wins4WinHandler());
	}

	@Override
	public GameType<Wins4ServerGame, Wins4ClientGame> getType() {
		return GameTypes.WINS_4;
	}
	
	/*@Override
	public void handlePacket(ServerPacket serverPacket) {
		ServerGame.super.handlePacket(serverPacket);
		if (serverPacket instanceof SelectGameFieldPacket packet) {
			Wins4FieldPos fieldPos = (Wins4FieldPos) packet.getFieldPos();
			Wins4ServerPlayer player = (Wins4ServerPlayer) this.getPlayerFor(packet.getProfile());
			if (Objects.equals(this.player, player)) {
				Optional<Wins4ServerField> optionalField = Util.reverseList(this.map.getFieldsForColumn(fieldPos.getColumn())).stream().filter(Wins4ServerField::isEmpty).findFirst();
				if (optionalField.isPresent()) {
					Wins4ServerField field = optionalField.orElseThrow(NullPointerException::new);
					if (field.isEmpty()) {
						Wins4ServerFigure figure = player.getUnplacedFigure();
						if (figure != null) {
							field.setFigure(figure);
							this.broadcastPlayers(new UpdateGameMapPacket(Util.mapList(this.getMap().getFields(), Wins4ServerField::getFieldInfo)));
							if (this.winHandler.hasPlayerFinished(player)) {
								this.winHandler.onPlayerFinished(player);
								LOGGER.info("Finished game {} with player win order: {}", this.getType().getInfoName(), Util.mapList(this.winHandler.getWinOrder(), this::getName));
								Wins4ResultLine resultLine = this.winHandler.getResultLine(this.map);
								LOGGER.debug("Result line of player {} is {}", this.getName(player), resultLine);
								if (resultLine != Wins4ResultLine.EMPTY) {
									for (Wins4ServerPlayer gamePlayer : this.players) {
										if (gamePlayer.equals(player))  {
											this.handlePlayerGameResult(gamePlayer, GameResult.WIN, resultLine, PlayerScore::increaseWin);
										} else {
											this.handlePlayerGameResult(gamePlayer, GameResult.LOSE, resultLine, PlayerScore::increaseLose);
										}
									}
								} else  {
									LOGGER.warn("Player {} finished the game but there is no result line", this.getName(player));
									this.stopGame();
								}
							} else if (this.winHandler.isDraw(map)) {
								for (Wins4ServerPlayer gamePlayer : this.players) {
									this.handlePlayerGameResult(gamePlayer, GameResult.DRAW, Wins4ResultLine.EMPTY, PlayerScore::increaseDraw);
								}
							} else {
								this.nextPlayer(false);
							}
						} else {
							LOGGER.warn("Fail to get unplaced figure for player {}, since all figures have been placed", this.getName(player));
							this.stopGame();
						}
					} else {
						LOGGER.warn("The field {} should be empty but there is a figure of player {} of it", fieldPos.getPosition(), this.getName(player));
						this.stopGame();
					}
				} else {
					LOGGER.warn("Fail to get empty field in column {}", fieldPos.getColumn());
					this.broadcastPlayer(new GameActionFailedPacket(), player);
				}
			} else {
				LOGGER.warn("Player {} tries to change the {} map at pos {} to {}, but it is not his turn", this.getName(player), fieldPos.getPosition(), player.getPlayerType());
			}
		}
	}
	
	protected void handlePlayerGameResult(Wins4ServerPlayer gamePlayer, GameResult result, Wins4ResultLine resultLine, Consumer<PlayerScore> consumer) {
		ServerPlayer player = gamePlayer.getPlayer();
		this.broadcastPlayer(new Wins4GameResultPacket(result, resultLine), gamePlayer);
		consumer.accept(player.getScore());
		this.broadcastPlayers(new SyncPlayerDataPacket(player.getProfile(), true, player.getScore()));
	}*/
	
	@Override
	public String toString() {
		return "Win4ServerGame";
	}
	
}
