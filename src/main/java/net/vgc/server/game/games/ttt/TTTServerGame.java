package net.vgc.server.game.games.ttt;

import java.util.List;

import net.vgc.client.game.games.ttt.TTTClientGame;
import net.vgc.game.games.ttt.player.TTTPlayerType;
import net.vgc.game.type.GameType;
import net.vgc.game.type.GameTypes;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.game.AbstractServerGame;
import net.vgc.server.game.games.ttt.map.TTTServerMap;
import net.vgc.server.game.games.ttt.player.TTTServerPlayer;
import net.vgc.server.game.games.ttt.win.TTTWinHandler;
import net.vgc.server.player.ServerPlayer;

public class TTTServerGame extends AbstractServerGame {
	
	public TTTServerGame(DedicatedServer server, List<ServerPlayer> players) {
		super(server, TTTServerMap::new, players, TTTPlayerType.values(), TTTServerPlayer::new, new TTTWinHandler());
	}

	@Override
	public GameType<TTTServerGame, TTTClientGame> getType() {
		return GameTypes.TIC_TAC_TOE;
	}
	
/*	@Override
	public void handlePacket(ServerPacket serverPacket) {
		ServerGame.super.handlePacket(serverPacket);
		if (serverPacket instanceof SelectGameFieldPacket packet) {
			TTTFieldPos fieldPos = (TTTFieldPos) packet.getFieldPos();
			TTTServerPlayer player = (TTTServerPlayer) this.getPlayerFor(packet.getProfile());
			if (Objects.equals(this.player, player)) {
				TTTServerField field = this.map.getField(null, null, fieldPos);
				if (field != null) {
					if (field.isEmpty()) {
						TTTServerFigure figure = player.getUnplacedFigure();
						if (figure != null) {
							field.setFigure(figure);
							this.broadcastPlayers(new UpdateGameMapPacket(Util.mapList(this.getMap().getFields(), TTTServerField::getFieldInfo)));
							if (this.winHandler.hasPlayerFinished(player)) {
								this.winHandler.onPlayerFinished(player);
								LOGGER.info("Finished game {} with player win order: {}", this.getType().getInfoName(), Util.mapList(this.winHandler.getWinOrder(), this::getName));
								TTTResultLine resultLine = this.winHandler.getResultLine(map);
								if (resultLine != TTTResultLine.EMPTY) {
									for (TTTServerPlayer gamePlayer : this.players) {
										if (gamePlayer.equals(player))  {
											this.handlePlayerGameResult(gamePlayer, GameResult.WIN, resultLine, PlayerScore::increaseWin);
										} else {
											this.handlePlayerGameResult(gamePlayer, GameResult.LOSE, resultLine, PlayerScore::increaseLose);
										}
									}
								} else {
									LOGGER.warn("Player {} finished the game but there is no result line", this.getName(player));
									this.stopGame();
								}
							} else if (this.winHandler.isDraw(this.map)) {
								for (TTTServerPlayer gamePlayer : this.players) {
									this.handlePlayerGameResult(gamePlayer, GameResult.DRAW, TTTResultLine.EMPTY, PlayerScore::increaseDraw);
								}
							} else {
								this.nextPlayer(false);
							}
						} else {
							LOGGER.warn("Fail to get unplaced figure for player {}, since all figures have been placed", this.getName(player));
							this.stopGame();
						}
					} else {
						LOGGER.warn("Fail to place a figure of player {} on field, since on the field is already a figure of type {}", this.getName(player), field.getFigure().getPlayerType());
						this.broadcastPlayer(new GameActionFailedPacket(), player);
					}
				} else {
					LOGGER.warn("Fail to get field for pos {}", fieldPos.getPosition());
					this.broadcastPlayer(new GameActionFailedPacket(), player);
				}
			} else {
				LOGGER.warn("Player {} tries to change the {} map at pos {} to {}, but it is not his turn", this.getName(player), fieldPos.getPosition(), player.getPlayerType());
			}
		}
	}
	
	protected void handlePlayerGameResult(TTTServerPlayer gamePlayer, GameResult result, TTTResultLine resultLine, Consumer<PlayerScore> consumer) {
		ServerPlayer player = gamePlayer.getPlayer();
		this.broadcastPlayer(new TTTGameResultPacket(result, resultLine), gamePlayer);
		consumer.accept(player.getScore());
		this.broadcastPlayers(new SyncPlayerDataPacket(player.getProfile(), true, player.getScore()));
	}*/
	
	@Override
	public String toString() {
		return "TTTServerGame";
	}

}
