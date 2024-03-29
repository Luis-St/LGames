package net.vgc.server.games.ludo.map.field;

import net.luis.utils.util.ToString;
import net.vgc.game.map.GameMap;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.figure.GameFigure;
import net.vgc.games.ludo.map.field.LudoFieldType;
import net.vgc.server.game.map.field.AbstractServerGameField;

/**
 *
 * @author Luis-st
 *
 */

public class LudoServerField extends AbstractServerGameField {
	
	public LudoServerField(GameMap map, GameFieldType fieldType, GamePlayerType colorType, GameFieldPos fieldPos) {
		super(map, fieldType, colorType, fieldPos);
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
	
	@Override
	public String toString() {
		return ToString.toString(this, "result");
	}
	
}
