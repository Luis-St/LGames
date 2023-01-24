package net.luis.server.games.wins4.map.field;

import net.luis.game.map.GameMap;
import net.luis.game.map.field.GameFieldInfo;
import net.luis.game.map.field.GameFieldPos;
import net.luis.game.map.field.GameFieldType;
import net.luis.game.player.GamePlayerType;
import net.luis.game.player.GameProfile;
import net.luis.game.player.figure.GameFigure;
import net.luis.games.wins4.map.field.Wins4FieldType;
import net.luis.games.wins4.player.Wins4PlayerType;
import net.luis.server.game.map.field.AbstractServerGameField;
import net.luis.utils.util.ToString;
import net.luis.utils.util.Utils;

/**
 *
 * @author Luis-st
 *
 */

public class Wins4ServerField extends AbstractServerGameField {
	
	public Wins4ServerField(GameMap map, GameFieldPos fieldPos) {
		super(map, Wins4FieldType.DEFAULT, Wins4PlayerType.NO, fieldPos);
	}
	
	@Override
	public final GameFieldType getFieldType() {
		LOGGER.warn("Fail to get field type of field {}, since 4 wins fields does not have a field type", this.getFieldPos().getPosition());
		return super.getFieldType();
	}
	
	@Override
	public final GamePlayerType getColorType() {
		LOGGER.warn("Fail to get field color type of field {}, since 4 wins fields does not have a field color type", this.getFieldPos().getPosition());
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
			return new GameFieldInfo(Wins4FieldType.DEFAULT, Wins4PlayerType.NO, this.getFieldPos(), GameProfile.EMPTY, -1, Utils.EMPTY_UUID);
		}
		GameFigure figure = this.getFigure();
		assert figure != null;
		return new GameFieldInfo(Wins4FieldType.DEFAULT, Wins4PlayerType.NO, this.getFieldPos(), figure.getPlayer().getPlayer().getProfile(), figure.getCount(), figure.getUUID());
	}
	
	@Override
	public String toString() {
		return ToString.toString(this, "fieldType", "colorType", "result");
	}
	
}
