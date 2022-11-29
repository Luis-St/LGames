package net.vgc.server.games.ludo.action;

import java.util.Objects;

import net.vgc.game.Game;
import net.vgc.game.GameResult;
import net.vgc.game.action.Action;
import net.vgc.game.action.data.gobal.EmptyData;
import net.vgc.game.action.data.specific.FieldInfoData;
import net.vgc.game.action.data.specific.GameResultData;
import net.vgc.game.action.data.specific.SelectFieldData;
import net.vgc.game.action.data.specific.SyncPlayerData;
import net.vgc.game.action.handler.AbstractActionHandler;
import net.vgc.game.action.type.ActionTypes;
import net.vgc.game.map.field.GameField;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.figure.GameFigure;
import net.vgc.game.score.PlayerScore;
import net.vgc.game.win.WinHandler;
import net.vgc.util.Util;

/**
 *
 * @author Luis-st
 *
 */

public class LudoServerActionHandler extends AbstractActionHandler {
	
	public LudoServerActionHandler(Game game) {
		super(game);
	}
	
	@Override
	public void handle(Action<?> action) {
		if (action.type() == ActionTypes.SELECT_FIELD && action.data() instanceof SelectFieldData data) {
			this.handleSelectField(data);
		}
	}
	
	private void handleSelectField(SelectFieldData data) {
		GameFieldPos fieldPos = data.getFieldPos();
		GameFieldType fieldType = data.getFieldType();
		GamePlayer player = this.getGame().getPlayerFor(data.getProfile());
		if (!Objects.equals(this.getGame().getPlayer(), player)) {
			LOGGER.warn("Player {} tries to change the {} map at pos {} to {}, but it is not his turn", player.getName(), fieldPos.getPosition(), player.getPlayerType());
			return;
		}
		int count = this.getGame().getDiceHandler().getLastCount(player);
		if (count == -1) {
			LOGGER.warn("Fail to move figure of player {}, since the player has not rolled the dice yet", player.getName());
			this.getGame().stop();
			return;
		}
		GameField currentField = this.getMap().getField(fieldType, player.getPlayerType(), fieldPos);
		if (currentField == null) {
			LOGGER.warn("Fail to get field for pos {}", fieldPos.getPosition());
			this.getGame().broadcastPlayer(player, ActionTypes.CAN_SELECT_FIELD, new EmptyData());
			return;
		}
		if (currentField.isEmpty()) {
			LOGGER.warn("Fail to get a figure of player {} from field {}, since the field is empty", player.getName(), currentField.getFieldPos().getPosition());
			this.getGame().broadcastPlayer(player, ActionTypes.CAN_SELECT_FIELD, new EmptyData());
			return;
		}
		GameFigure figure = currentField.getFigure();
		GameField nextField = this.getMap().getNextField(figure, count);
		if (nextField == null) {
			LOGGER.warn("Fail to move figure {} of player {}, since there is no next field for the figure", figure.getCount(), player.getName());
			this.getGame().broadcastPlayer(player, ActionTypes.CAN_SELECT_FIELD, new EmptyData());
			return;
		}
		if (!this.getMap().moveFigureTo(figure, nextField)) {
			LOGGER.warn("Fail to move figure {} of player {} to field {}", figure.getCount(), player.getName(), nextField.getFieldPos().getPosition());
			this.getGame().broadcastPlayer(player, ActionTypes.CAN_SELECT_FIELD, new EmptyData());
			return;
		}
		this.getGame().broadcastPlayer(player, ActionTypes.UPDATE_MAP, new FieldInfoData(Util.mapList(this.getMap().getFields(), GameField::getFieldInfo)));
		WinHandler winHandler = this.getGame().getWinHandler();
		if (winHandler.hasPlayerFinished(player)) {
			winHandler.onPlayerFinished(player);
			if (winHandler.getWinOrder().size() - this.getGame().getPlayers().size() > 1) {
				this.getGame().nextPlayer(false);
			} else {
				LOGGER.info("Finished game {} with player win order: {}", this.getGame().getType().getInfoName(), Util.mapList(winHandler.getWinOrder(), GamePlayer::getName));
				for (GamePlayer gamePlayer : this.getGame().getPlayers()) {
					PlayerScore score = gamePlayer.getPlayer().getScore();
					score.setScore(winHandler.getScoreFor(this.getGame(), gamePlayer));
					this.getGame().broadcastPlayers(ActionTypes.SYNC_PLAYER, new SyncPlayerData(gamePlayer.getPlayer().getProfile(), true, score));
				}
			}
			this.getGame().broadcastPlayers(ActionTypes.GAME_RESULT, new GameResultData(GameResult.NO, null));
		} else if (this.getGame().getDiceHandler().canRollAfterMove(player, currentField, nextField, count)) {
			player.setRollCount(1);
			this.getGame().broadcastPlayer(player, ActionTypes.CAN_ROLL_DICE_AGAIN, new EmptyData());
		} else {
			this.getGame().nextPlayer(false);
		}
	}
	
}
