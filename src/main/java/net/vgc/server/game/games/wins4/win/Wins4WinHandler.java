package net.vgc.server.game.games.wins4.win;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.vgc.game.Game;
import net.vgc.game.games.wins4.Wins4ResultLine;
import net.vgc.game.games.wins4.map.field.Wins4FieldPos;
import net.vgc.game.games.wins4.player.Wins4PlayerType;
import net.vgc.game.map.GameMap;
import net.vgc.game.map.field.GameField;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.server.game.games.wins4.map.Wins4ServerMap;
import net.vgc.server.game.games.wins4.player.Wins4ServerPlayer;
import net.vgc.server.game.win.AbstractWinHandler;
import net.vgc.util.Util;

public class Wins4WinHandler extends AbstractWinHandler {
	
	protected final List<Wins4ResultLine> resultLines = Util.make(Lists.newArrayList(), (list) -> {
		for (int i = 0; i < 42; i++) {
			list.addAll(this.getResultLinesForPos(Wins4FieldPos.of(i)));
		}
	});
	
	protected List<Wins4ResultLine> getResultLinesForPos(Wins4FieldPos fieldPos) {
		int position = fieldPos.getPosition();
		int row = fieldPos.getRow();
		int column = fieldPos.getColumn();
		return Util.make(new ArrayList<Wins4ResultLine>(), (list) -> {
			list.add(new Wins4ResultLine(fieldPos, Wins4FieldPos.of(row - 1, column), Wins4FieldPos.of(row - 2, column), Wins4FieldPos.of(row - 3, column)));
			list.add(new Wins4ResultLine(fieldPos, Wins4FieldPos.of(row - 1, column + 1), Wins4FieldPos.of(row - 2, column + 2), Wins4FieldPos.of(row - 3, column + 3)));
			list.add(new Wins4ResultLine(fieldPos, Wins4FieldPos.of(position + 1), Wins4FieldPos.of(position + 2), Wins4FieldPos.of(position + 3)));
			list.add(new Wins4ResultLine(fieldPos, Wins4FieldPos.of(row + 1, column + 1), Wins4FieldPos.of(row + 2, column + 2), Wins4FieldPos.of(row + 3, column + 3)));
			list.add(new Wins4ResultLine(fieldPos, Wins4FieldPos.of(row + 1, column), Wins4FieldPos.of(row + 2, column), Wins4FieldPos.of(row + 3, column)));
			list.add(new Wins4ResultLine(fieldPos, Wins4FieldPos.of(row + 1, column - 1), Wins4FieldPos.of(row + 2, column - 2), Wins4FieldPos.of(row + 3, column - 3)));
			list.add(new Wins4ResultLine(fieldPos, Wins4FieldPos.of(position - 1), Wins4FieldPos.of(position - 2), Wins4FieldPos.of(position - 3)));
			list.add(new Wins4ResultLine(fieldPos, Wins4FieldPos.of(row - 1, column - 1), Wins4FieldPos.of(row - 2, column - 2), Wins4FieldPos.of(row - 3, column - 3)));
		}).stream().filter((resultLine) -> {
			return !resultLine.isOutOfMap();
		}).collect(Collectors.toList());
	}
	
	@Override
	public boolean hasMultipleWinners() {
		return false;
	}

	@Override
	public boolean hasPlayerFinished(GamePlayer gamePlayer) {
		if (gamePlayer instanceof Wins4ServerPlayer player) {
			return this.getWinType((Wins4ServerMap) player.getMap()) == player.getPlayerType();
		}
		return false;
	}
	
	@Override
	public boolean isDraw(GameMap gameMap) {
		if (gameMap instanceof Wins4ServerMap map) {
			return !map.hasEmptyField() && this.getWinType(map) == Wins4PlayerType.NO;
		}
		return false;
	}
	
	protected GamePlayerType getWinType(Wins4ServerMap map) {
		Wins4ResultLine resultLine = this.getResultLine(map);
		if (resultLine != Wins4ResultLine.EMPTY) {
			return this.getFieldType(map, resultLine.getFirstPos());
		}
		return Wins4PlayerType.NO;
	}
	
	public Wins4ResultLine getResultLine(Wins4ServerMap map) {
		for (Wins4ResultLine resultLine : this.resultLines) {
			if (this.getLineWinType(map, resultLine) != Wins4PlayerType.NO) {
				return resultLine;
			}
		}
		return Wins4ResultLine.EMPTY;
	}
	
	protected GamePlayerType getLineWinType(Wins4ServerMap map, Wins4ResultLine resultLine) {
		GamePlayerType playerType = this.getFieldType(map, resultLine.getFirstPos());
		if (playerType != Wins4PlayerType.NO) {
			if (this.getFieldType(map, resultLine.getSecondPos()) == playerType && this.getFieldType(map, resultLine.getThirdPos()) == playerType && this.getFieldType(map, resultLine.getFourthPos()) == playerType) {
				return playerType;
			}
		}
		return Wins4PlayerType.NO;
	}
	
	protected GamePlayerType getFieldType(Wins4ServerMap map, Wins4FieldPos fieldPos) {
		GameField field = map.getField(null, null, fieldPos);
		if (field != null) {
			if (field.isEmpty()) {
				return Wins4PlayerType.NO;
			}
			GamePlayerType playerType = field.getFigure().getPlayerType();
			return playerType == null ? Wins4PlayerType.NO : playerType;
		}
		return Wins4PlayerType.NO;
	}

	@Override
	public int getScoreFor(Game game, GamePlayer player) {
		return this.getFinishedPlayers().contains(player) ? 1 : 0;
	}

}
