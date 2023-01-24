package net.luis.ludo.map.field;

import net.luis.game.map.GameMap;
import net.luis.game.map.field.AbstractGameField;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.map.field.GameFieldType;
import net.luis.game.player.GamePlayerType;
import net.luis.game.player.figure.GameFigure;

/**
 *
 * @author Luis-st
 *
 */

public class LudoField extends AbstractGameField {
	
	public LudoField(GameMap map, GameFieldType fieldType, GamePlayerType colorType, GameFieldPos fieldPos, double fieldSize) {
		super(map, fieldType, colorType, fieldPos, fieldSize);
	}
	
	@Override
	public boolean isHome() {
		return this.getFieldType() == LudoFieldType.HOME;
	}
	
	@Override
	public boolean isStart() {
		return this.getFieldPos().isStart();
	}
	
	@Override
	public boolean isStartFor(GameFigure figure) {
		return figure.getStartPos().equals(this.getFieldPos());
	}
	
	@Override
	public boolean isWin() {
		return this.getFieldType() == LudoFieldType.WIN;
	}
}
