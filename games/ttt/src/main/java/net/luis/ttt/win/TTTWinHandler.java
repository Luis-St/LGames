package net.luis.ttt.win;

import com.google.common.collect.Lists;
import net.luis.game.Game;
import net.luis.game.map.GameMap;
import net.luis.game.map.field.GameField;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.player.game.GamePlayer;
import net.luis.game.player.game.GamePlayerType;
import net.luis.game.win.AbstractWinHandler;
import net.luis.game.win.GameResultLine;
import net.luis.ttt.map.field.TTTFieldPos;
import net.luis.ttt.player.TTTPlayerType;
import net.luis.utils.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class TTTWinHandler extends AbstractWinHandler {
	
	private final List<GameResultLine> resultLines = Utils.make(Lists.newArrayList(), (list) -> {
		list.add(new GameResultLine(TTTFieldPos.of(0), TTTFieldPos.of(1), TTTFieldPos.of(2)));
		list.add(new GameResultLine(TTTFieldPos.of(3), TTTFieldPos.of(4), TTTFieldPos.of(5)));
		list.add(new GameResultLine(TTTFieldPos.of(6), TTTFieldPos.of(7), TTTFieldPos.of(8)));
		list.add(new GameResultLine(TTTFieldPos.of(0), TTTFieldPos.of(3), TTTFieldPos.of(6)));
		list.add(new GameResultLine(TTTFieldPos.of(1), TTTFieldPos.of(4), TTTFieldPos.of(7)));
		list.add(new GameResultLine(TTTFieldPos.of(2), TTTFieldPos.of(5), TTTFieldPos.of(8)));
		list.add(new GameResultLine(TTTFieldPos.of(0), TTTFieldPos.of(4), TTTFieldPos.of(8)));
		list.add(new GameResultLine(TTTFieldPos.of(2), TTTFieldPos.of(4), TTTFieldPos.of(6)));
	});
	
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
		return !map.hasEmptyField() && this.getWinType(map) == TTTPlayerType.NO;
	}
	
	private GamePlayerType getWinType(GameMap map) {
		GameResultLine resultLine = this.getResultLine(map);
		if (resultLine != GameResultLine.EMPTY) {
			return this.getFieldType(map, resultLine.getPos(0));
		}
		return TTTPlayerType.NO;
	}
	
	public @NotNull GameResultLine getResultLine(@NotNull GameMap map) {
		for (GameResultLine resultLine : this.resultLines) {
			if (this.getLineWinType(map, resultLine) != TTTPlayerType.NO) {
				return resultLine;
			}
		}
		return GameResultLine.EMPTY;
	}
	
	private GamePlayerType getLineWinType(GameMap map, GameResultLine resultLine) {
		GamePlayerType playerType = this.getFieldType(map, resultLine.getPos(0));
		if (playerType != TTTPlayerType.NO) {
			if (this.getFieldType(map, resultLine.getPos(1)) == playerType && this.getFieldType(map, resultLine.getPos(2)) == playerType) {
				return playerType;
			}
		}
		return TTTPlayerType.NO;
	}
	
	private GamePlayerType getFieldType(GameMap map, GameFieldPos fieldPos) {
		GameField field = map.getField(null, null, fieldPos);
		if (field != null) {
			if (field.isEmpty()) {
				return TTTPlayerType.NO;
			}
			GamePlayerType playerType = Objects.requireNonNull(field.getFigure()).getPlayerType();
			return playerType == null ? TTTPlayerType.NO : playerType;
		}
		return TTTPlayerType.NO;
	}
	
	@Override
	public int getScoreFor(@NotNull Game game, @NotNull GamePlayer player) {
		return this.getFinishedPlayers().contains(player) ? 1 : 0;
	}
	
}