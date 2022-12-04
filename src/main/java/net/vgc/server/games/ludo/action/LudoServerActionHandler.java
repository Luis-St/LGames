package net.vgc.server.games.ludo.action;

import java.util.Objects;

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
import net.vgc.game.win.WinHandler;
import net.vgc.util.Util;

/**
 *
 * @author Luis-st
 *
 */

public class LudoServerActionHandler extends AbstractGameActionHandler {
	
	public LudoServerActionHandler(Game game) {
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
		int count = this.getGame().getDiceHandler().getLastCount(player);
		if (count == -1) {
			LOGGER.warn("Fail to move figure of player {}, since the player has not rolled the dice yet", player.getName());
			this.getGame().stop();
			return false;
		}
		GameField currentField = this.getMap().getField(fieldType, player.getPlayerType(), fieldPos);
		if (currentField == null) {
			LOGGER.warn("Fail to get field for pos {}", fieldPos.getPosition());
			this.getGame().broadcastPlayer(player, GameActionTypes.CAN_SELECT_FIELD, new EmptyData());
			return false;
		}
		if (currentField.isEmpty()) {
			LOGGER.warn("Fail to get a figure of player {} from field {}, since the field is empty", player.getName(), currentField.getFieldPos().getPosition());
			this.getGame().broadcastPlayer(player, GameActionTypes.CAN_SELECT_FIELD, new EmptyData());
			return false;
		}
		GameFigure figure = currentField.getFigure();
		GameField nextField = this.getMap().getNextField(figure, count);
		if (nextField == null) {
			LOGGER.warn("Fail to move figure {} of player {}, since there is no next field for the figure", figure.getCount(), player.getName());
			this.getGame().broadcastPlayer(player, GameActionTypes.CAN_SELECT_FIELD, new EmptyData());
			return false;
		}
		if (!this.getMap().moveFigureTo(figure, nextField)) {
			LOGGER.warn("Fail to move figure {} of player {} to field {}", figure.getCount(), player.getName(), nextField.getFieldPos().getPosition());
			this.getGame().broadcastPlayer(player, GameActionTypes.CAN_SELECT_FIELD, new EmptyData());
			return false;
		}
		this.getGame().broadcastPlayer(player, GameActionTypes.UPDATE_MAP, new FieldInfoData(Util.mapList(this.getMap().getFields(), GameField::getFieldInfo)));
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
					this.getGame().broadcastPlayers(GameActionTypes.SYNC_PLAYER, new SyncPlayerData(gamePlayer.getPlayer().getProfile(), true, score));
				}
			}
			this.getGame().broadcastPlayers(GameActionTypes.GAME_RESULT, new GameResultData(GameResult.NO, null));
		} else if (this.getGame().getDiceHandler().canRollAfterMove(player, currentField, nextField, count)) {
			player.setRollCount(1);
			this.getGame().broadcastPlayer(player, GameActionTypes.CAN_ROLL_DICE_AGAIN, new EmptyData());
		} else {
			this.getGame().nextPlayer(false);
		}
		return true;
	}
	
}
