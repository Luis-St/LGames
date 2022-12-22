package net.vgc.server.games.ttt.map.field;

import net.luis.utils.util.ToString;
import net.vgc.game.map.GameMap;
import net.vgc.game.map.field.GameFieldInfo;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.GamePlayerType;
import net.vgc.game.player.figure.GameFigure;
import net.vgc.games.ttt.map.field.TTTFieldType;
import net.vgc.games.ttt.player.TTTPlayerType;
import net.vgc.player.GameProfile;
import net.vgc.server.game.map.field.AbstractServerGameField;
import net.vgc.util.Util;

/**
 *
 * @author Luis-st
 *
 */

public class TTTServerField extends AbstractServerGameField {
	
	public TTTServerField(GameMap map, GameFieldPos fieldPos) {
		super(map, TTTFieldType.DEFAULT, TTTPlayerType.NO, fieldPos);
	}
	
	@Override
	public final GameFieldType getFieldType() {
		LOGGER.warn("Fail to get field type of field {}, since tic tac toe fields does not have a field type", this.getFieldPos().getPosition());
		return super.getFieldType();
	}
	
	@Override
	public final GamePlayerType getColorType() {
		LOGGER.warn("Fail to get field color type of field {}, since tic tac toe fields does not have a field color type", this.getFieldPos().getPosition());
		return super.getColorType();
	}
	
	@Override
	public boolean isHome() {
		return false;
	}
	
	@Override
	public boolean isStart() {
		return false;
	}
	
	@Override
	public boolean isStartFor(GameFigure figure) {
		return false;
	}
	
	@Override
	public boolean isWin() {
		return false;
	}
	
	@Override
	public GameFieldInfo getFieldInfo() {
		if (this.isEmpty()) {
			return new GameFieldInfo(TTTFieldType.DEFAULT, TTTPlayerType.NO, this.getFieldPos(), GameProfile.EMPTY, -1, Util.EMPTY_UUID);
		}
		GameFigure figure = this.getFigure();
		return new GameFieldInfo(TTTFieldType.DEFAULT, TTTPlayerType.NO, this.getFieldPos(), figure.getPlayer().getPlayer().getProfile(), figure.getCount(), figure.getUUID());
	}
	
	@Override
	public String toString() {
		return ToString.toString(this, "fieldType", "colorType", "result");
	}
	
}
