package net.vgc.server.games.ttt.action;

import java.util.Objects;
import java.util.function.Consumer;

import net.vgc.game.Game;
import net.vgc.game.GameResult;
import net.vgc.game.action.GameAction;
import net.vgc.game.action.data.gobal.EmptyData;
import net.vgc.game.action.data.specific.FieldInfoData;
import net.vgc.game.action.data.specific.GameResultData;
import net.vgc.game.action.data.specific.SelectFieldData;
import net.vgc.game.action.data.specific.SyncPlayerData;
import net.vgc.game.action.handler.AbstractGameActionHandler;
import net.vgc.game.action.type.GameActionTypes;
import net.vgc.game.map.field.GameField;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.figure.GameFigure;
import net.vgc.game.score.PlayerScore;
import net.vgc.game.win.GameResultLine;
import net.vgc.game.win.WinHandler;
import net.vgc.player.Player;
import net.vgc.util.Util;

/**
 *
 * @author Luis-st
 *
 */

public class TTTServerActionHandler extends AbstractGameActionHandler {
	
	public TTTServerActionHandler(Game game) {
		super(game);
	}
	
	@Override
	public boolean handle(GameAction<?> action) {
		if (action.type() == GameActionTypes.SELECT_FIELD && action.data() instanceof SelectFieldData data) {
			return this.handleSelectField(data);
		}
		LOGGER.warn("Fail to handle action of type {}", action.type().getName());
		return false;
	}
	
	private boolean handleSelectField(SelectFieldData data) {
		GameFieldPos fieldPos = data.getFieldPos();
		GameFieldType fieldType = data.getFieldType();
		GamePlayer player = this.getGame().getPlayerFor(data.getProfile());
		if (!Objects.equals(this.getGame().getPlayer(), player)) {
			LOGGER.warn("Player {} tries to change the {} map at pos {} to {}, but it is not his turn", player.getName(), fieldPos.getPosition(), player.getPlayerType());
			return false;
		}
		GameField field = this.getMap().getField(fieldType, player.getPlayerType(), fieldPos);
		if (field == null) {
			LOGGER.warn("Fail to get field for pos {}", fieldPos.getPosition());
			this.getGame().broadcastPlayer(player, GameActionTypes.CAN_SELECT_FIELD, new EmptyData());
			return false;
		}
		if (!field.isEmpty()) {
			LOGGER.warn("Fail to place a figure of player {} on field, since on the field is already a figure of type {}", player.getName(), field.getFigure().getPlayerType());
			this.getGame().broadcastPlayer(player, GameActionTypes.CAN_SELECT_FIELD, new EmptyData());
			return false;
		}
		GameFigure figure = player.getFigure((map, gameFigure) -> {
			return map.getField(gameFigure) == null;
		});
		if (figure == null) {
			LOGGER.warn("Fail to get unplaced figure for player {}, since all figures have been placed", player.getName());
			this.getGame().stop();
			return false;
		}
		field.setFigure(figure);
		this.getGame().broadcastPlayer(player, GameActionTypes.UPDATE_MAP, new FieldInfoData(Util.mapList(this.getMap().getFields(), GameField::getFieldInfo)));
		WinHandler winHandler = this.getGame().getWinHandler();
		if (winHandler.hasPlayerFinished(player)) {
			winHandler.onPlayerFinished(player);
			LOGGER.info("Finished game {} with player win order: {}", this.getGame().getType().getInfoName(), Util.mapList(winHandler.getWinOrder(), GamePlayer::getName));
			GameResultLine resultLine = winHandler.getResultLine(this.getMap());
			if (resultLine == GameResultLine.EMPTY) {
				LOGGER.warn("Player {} finished the game but there is no result line", player.getName());
				this.getGame().stop();
			} else {
				for (GamePlayer gamePlayer : this.getGame().getPlayers()) {
					if (gamePlayer.equals(player)) {
						this.updateAndBroadcast(gamePlayer, GameResult.WIN, resultLine, PlayerScore::increaseWin);
					} else {
						this.updateAndBroadcast(gamePlayer, GameResult.LOSE, resultLine, PlayerScore::increaseLose);
					}
				}
			}
		} else if (winHandler.isDraw(this.getMap())) {
			for (GamePlayer gamePlayer : this.getGame().getPlayers()) {
				this.updateAndBroadcast(gamePlayer, GameResult.DRAW, GameResultLine.EMPTY, PlayerScore::increaseDraw);
			}
		} else {
			this.getGame().nextPlayer(false);
		}
		return true;
	}
	
	// TODO: rename
	private void updateAndBroadcast(GamePlayer gamePlayer, GameResult result, GameResultLine resultLine, Consumer<PlayerScore> consumer) {
		Player player = gamePlayer.getPlayer();
		this.getGame().broadcastPlayer(gamePlayer, GameActionTypes.GAME_RESULT, new GameResultData(result, resultLine));
		consumer.accept(player.getScore());
		this.getGame().broadcastPlayers(GameActionTypes.SYNC_PLAYER, new SyncPlayerData(player.getProfile(), true, player.getScore()));
	}
	
}
