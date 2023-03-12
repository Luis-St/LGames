package net.luis.wins4.win;

import com.google.common.collect.Lists;
import net.luis.game.Game;
import net.luis.game.map.GameMap;
import net.luis.game.map.field.GameField;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.game.GamePlayer;
import net.luis.game.player.game.GamePlayerType;
import net.luis.game.win.AbstractWinHandler;
import net.luis.game.win.GameResultLine;
import net.luis.utils.util.Utils;
import net.luis.wins4.map.field.Wins4FieldPos;
import net.luis.wins4.player.Wins4PlayerType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-st
 *
 */

public class Wins4WinHandler extends AbstractWinHandler {
	
	private final List<GameResultLine> resultLines = Utils.make(Lists.newArrayList(), (list) -> {
		for (int i = 0; i < 42; i++) {
			list.addAll(this.getResultLinesForPos(Wins4FieldPos.of(i)));
		}
	});
	
	private List<GameResultLine> getResultLinesForPos(Wins4FieldPos fieldPos) {
		int position = fieldPos.getPosition();
		int row = fieldPos.getRow();
		int column = fieldPos.getColumn();
		return Utils.make(new ArrayList<GameResultLine>(), (list) -> {
			list.add(new GameResultLine(fieldPos, Wins4FieldPos.of(row - 1, column), Wins4FieldPos.of(row - 2, column), Wins4FieldPos.of(row - 3, column)));
			list.add(new GameResultLine(fieldPos, Wins4FieldPos.of(row - 1, column + 1), Wins4FieldPos.of(row - 2, column + 2), Wins4FieldPos.of(row - 3, column + 3)));
			list.add(new GameResultLine(fieldPos, Wins4FieldPos.of(position + 1), Wins4FieldPos.of(position + 2), Wins4FieldPos.of(position + 3)));
			list.add(new GameResultLine(fieldPos, Wins4FieldPos.of(row + 1, column + 1), Wins4FieldPos.of(row + 2, column + 2), Wins4FieldPos.of(row + 3, column + 3)));
			list.add(new GameResultLine(fieldPos, Wins4FieldPos.of(row + 1, column), Wins4FieldPos.of(row + 2, column), Wins4FieldPos.of(row + 3, column)));
			list.add(new GameResultLine(fieldPos, Wins4FieldPos.of(row + 1, column - 1), Wins4FieldPos.of(row + 2, column - 2), Wins4FieldPos.of(row + 3, column - 3)));
			list.add(new GameResultLine(fieldPos, Wins4FieldPos.of(position - 1), Wins4FieldPos.of(position - 2), Wins4FieldPos.of(position - 3)));
			list.add(new GameResultLine(fieldPos, Wins4FieldPos.of(row - 1, column - 1), Wins4FieldPos.of(row - 2, column - 2), Wins4FieldPos.of(row - 3, column - 3)));
		}).stream().filter((resultLine) -> {
			return resultLine.getPositions().stream().filter(GameFieldPos::isOutOfMap).findAny().isEmpty();
		}).collect(Collectors.toList());
	}
	
	@Override
	public boolean hasMultipleWinners() {
		return false;
	}
	
	@Override
	public boolean hasPlayerFinished(@NotNull GamePlayer gamePlayer) {
		return this.getWinType(gamePlayer.getMap()) == gamePlayer.getPlayerType();
	}
	
	@Override
	public boolean isDraw(@NotNull GameMap map) {
		return !map.hasEmptyField() && this.getWinType(map) == Wins4PlayerType.NO;
	}
	
	private GamePlayerType getWinType(GameMap map) {
		GameResultLine resultLine = this.getResultLine(map);
		if (resultLine != GameResultLine.EMPTY) {
			return this.getFieldType(map, resultLine.getPos(0));
		}
		return Wins4PlayerType.NO;
	}
	
	public @NotNull GameResultLine getResultLine(@NotNull GameMap map) {
		for (GameResultLine resultLine : this.resultLines) {
			if (this.getLineWinType(map, resultLine) != Wins4PlayerType.NO) {
				return resultLine;
			}
		}
		return GameResultLine.EMPTY;
	}
	
	private GamePlayerType getLineWinType(GameMap map, GameResultLine resultLine) {
		GamePlayerType playerType = this.getFieldType(map, resultLine.getPos(0));
		if (playerType != Wins4PlayerType.NO) {
			if (this.getFieldType(map, resultLine.getPos(1)) == playerType && this.getFieldType(map, resultLine.getPos(2)) == playerType && this.getFieldType(map, resultLine.getPos(2)) == playerType) {
				return playerType;
			}
		}
		return Wins4PlayerType.NO;
	}
	
	private GamePlayerType getFieldType(GameMap map, GameFieldPos fieldPos) {
		GameField field = map.getField(null, null, fieldPos);
		if (field != null) {
			if (field.isEmpty()) {
				return Wins4PlayerType.NO;
			}
			GamePlayerType playerType = Objects.requireNonNull(field.getFigure()).getPlayerType();
			return playerType == null ? Wins4PlayerType.NO : playerType;
		}
		return Wins4PlayerType.NO;
	}
	
	@Override
	public int getScoreFor(@NotNull Game game, @NotNull GamePlayer player) {
		return this.getFinishedPlayers().contains(player) ? 1 : 0;
	}
	
}
