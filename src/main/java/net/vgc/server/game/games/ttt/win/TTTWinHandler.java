package net.vgc.server.game.games.ttt.win;

import java.util.List;

import com.google.common.collect.Lists;

import net.vgc.game.Game;
import net.vgc.game.games.ttt.TTTResultLine;
import net.vgc.game.games.ttt.map.field.TTTFieldPos;
import net.vgc.game.games.ttt.player.TTTPlayerType;
import net.vgc.game.map.GameMap;
import net.vgc.game.map.field.GameField;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.server.game.games.ttt.map.TTTServerMap;
import net.vgc.server.game.games.ttt.player.TTTServerPlayer;
import net.vgc.server.game.win.AbstractWinHandler;
import net.vgc.util.Util;

public class TTTWinHandler extends AbstractWinHandler {
	
	protected final List<TTTResultLine> resultLines = Util.make(Lists.newArrayList(), (list) -> {
		list.add(new TTTResultLine(TTTFieldPos.of(0), TTTFieldPos.of(1), TTTFieldPos.of(2)));
		list.add(new TTTResultLine(TTTFieldPos.of(3), TTTFieldPos.of(4), TTTFieldPos.of(5)));
		list.add(new TTTResultLine(TTTFieldPos.of(6), TTTFieldPos.of(7), TTTFieldPos.of(8)));
		list.add(new TTTResultLine(TTTFieldPos.of(0), TTTFieldPos.of(3), TTTFieldPos.of(6)));
		list.add(new TTTResultLine(TTTFieldPos.of(1), TTTFieldPos.of(4), TTTFieldPos.of(7)));
		list.add(new TTTResultLine(TTTFieldPos.of(2), TTTFieldPos.of(5), TTTFieldPos.of(8)));
		list.add(new TTTResultLine(TTTFieldPos.of(0), TTTFieldPos.of(4), TTTFieldPos.of(8)));
		list.add(new TTTResultLine(TTTFieldPos.of(2), TTTFieldPos.of(4), TTTFieldPos.of(6)));
	});
	
	@Override
	public boolean hasMultipleWinners() {
		return false;
	}
	
	@Override
	public boolean hasPlayerFinished(GamePlayer gamePlayer) {
		if (gamePlayer instanceof TTTServerPlayer player) {
			return this.getWinType((TTTServerMap) player.getMap()) == player.getPlayerType();
		}
		return false;
	}
	
	@Override
	public boolean isDraw(GameMap gameMap) {
		if (gameMap instanceof TTTServerMap map) {
			return !map.hasEmptyField() && this.getWinType(map) == TTTPlayerType.NO;
		}
		return false;
	}
	
	protected GamePlayerType getWinType(GameMap map) {
		TTTResultLine resultLine = this.getResultLine(map);
		if (resultLine != TTTResultLine.EMPTY) {
			return this.getFieldType(map, resultLine.getFirstPos());
		}
		return TTTPlayerType.NO;
	}
	
	public TTTResultLine getResultLine(GameMap map) {
		for (TTTResultLine resultLine : this.resultLines) {
			if (this.getLineWinType(map, resultLine) != TTTPlayerType.NO) {
				return resultLine;
			}
		}
		return TTTResultLine.EMPTY;
	}
	
	protected GamePlayerType getLineWinType(GameMap map, TTTResultLine resultLine) {
		GamePlayerType playerType = this.getFieldType(map, resultLine.getFirstPos());
		if (playerType != TTTPlayerType.NO) {
			if (this.getFieldType(map, resultLine.getSecondPos()) == playerType && this.getFieldType(map, resultLine.getThirdPos()) == playerType) {
				return playerType;
			}
		}
		return TTTPlayerType.NO;
	}
	
	protected GamePlayerType getFieldType(GameMap map, TTTFieldPos fieldPos) {
		GameField field = map.getField(null, null, fieldPos);
		if (field != null) {
			if (field.isEmpty()) {
				return TTTPlayerType.NO;
			}
			GamePlayerType playerType = field.getFigure().getPlayerType();
			return playerType == null ? TTTPlayerType.NO : playerType;
		}
		return TTTPlayerType.NO;
	}
	
	@Override
	public int getScoreFor(Game game, GamePlayer player) {
		return this.getFinishedPlayers().contains(player) ? 1 : 0;
	}

}
