package net.vgc.server.games.wins4.action;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

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
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.figure.GameFigure;
import net.vgc.game.score.PlayerScore;
import net.vgc.game.win.GameResultLine;
import net.vgc.game.win.WinHandler;
import net.vgc.games.wins4.map.field.Wins4FieldPos;
import net.vgc.games.wins4.map.field.Wins4FieldType;
import net.vgc.player.Player;
import net.vgc.util.Mth;
import net.vgc.util.Util;

/**
 *
 * @author Luis-st
 *
 */

public class Wins4ServerActionHandler extends AbstractActionHandler {
	
	public Wins4ServerActionHandler(Game game) {
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
		GamePlayer player = this.getGame().getPlayerFor(data.getProfile());
		if (!Objects.equals(this.getGame().getPlayer(), player)) {
			LOGGER.warn("Player {} tries to change the {} map at pos {} to {}, but it is not his turn", player.getName(), fieldPos.getPosition(), player.getPlayerType());
			return;
		}
		Optional<GameField> optionalField = Util.reverseList(this.getFieldsForColumn(fieldPos.getColumn())).stream().filter(GameField::isEmpty).findFirst();
		if (!optionalField.isPresent()) {
			LOGGER.warn("Fail to get empty field in column {}", fieldPos.getColumn());
			this.getGame().broadcastPlayer(player, ActionTypes.CAN_SELECT_FIELD, new EmptyData());
			return;
		}
		GameField field = optionalField.orElseThrow(NullPointerException::new);
		if (!field.isEmpty()) {
			LOGGER.warn("The field {} should be empty but there is a figure of player {} of it", fieldPos.getPosition(), player.getName());
			this.getGame().stop();
			return;
		}
		GameFigure figure = player.getFigure((map, gameFigure) -> {
			return map.getField(gameFigure) == null;
		});
		if (figure == null) {
			LOGGER.warn("Fail to get unplaced figure for player {}, since all figures have been placed", player.getName());
			this.getGame().stop();
			return;
		}
		field.setFigure(figure);
		this.getGame().broadcastPlayer(player, ActionTypes.UPDATE_MAP, new FieldInfoData(Util.mapList(this.getMap().getFields(), GameField::getFieldInfo)));
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
	
	private void updateAndBroadcast(GamePlayer gamePlayer, GameResult result, GameResultLine resultLine, Consumer<PlayerScore> consumer) {
		Player player = gamePlayer.getPlayer();
		this.getGame().broadcastPlayer(gamePlayer, ActionTypes.GAME_RESULT, new GameResultData(result, resultLine));
		consumer.accept(player.getScore());
		this.getGame().broadcastPlayers(ActionTypes.SYNC_PLAYER, new SyncPlayerData(player.getProfile(), true, player.getScore()));
	}
	
}
