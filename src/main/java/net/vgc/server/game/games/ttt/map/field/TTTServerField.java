package net.vgc.server.game.games.ttt.map.field;

import java.util.Objects;

import net.vgc.game.games.ttt.map.field.TTTFieldPos;
import net.vgc.game.games.ttt.map.field.TTTFieldType;
import net.vgc.game.games.ttt.player.TTTPlayerType;
import net.vgc.game.map.field.GameFieldInfo;
import net.vgc.game.player.field.GameFigure;
import net.vgc.player.GameProfile;
import net.vgc.server.game.games.ttt.player.figure.TTTServerFigure;
import net.vgc.server.game.map.field.ServerGameField;
import net.vgc.util.Util;

public class TTTServerField implements ServerGameField {
	
	protected final TTTFieldPos fieldPos;
	protected TTTServerFigure figure;
	
	public TTTServerField(TTTFieldPos fieldPos) {
		this.fieldPos = fieldPos;
	}
	
	@Override
	public TTTFieldType getFieldType() {
		LOGGER.warn("Fail to get field type of field {}, since tic tac toe fields does not have a field type", this.getFieldPos().getPosition());
		return TTTFieldType.DEFAULT;
	}

	@Override
	public TTTFieldPos getFieldPos() {
		return this.fieldPos;
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
	public TTTServerFigure getFigure() {
		return this.figure;
	}

	@Override
	public void setFigure(GameFigure figure) {
		this.figure = (TTTServerFigure) figure;
	}
	
	@Override
	public GameFieldInfo getFieldInfo() {
		if (this.isEmpty()) {
			return new GameFieldInfo(TTTFieldType.DEFAULT, TTTPlayerType.NO, this.fieldPos, GameProfile.EMPTY, -1, Util.EMPTY_UUID);
		}
		TTTServerFigure figure = this.getFigure();
		return new GameFieldInfo(TTTFieldType.DEFAULT, TTTPlayerType.NO, this.fieldPos, figure.getPlayer().getPlayer().getProfile(), figure.getCount(), figure.getUUID());
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof TTTServerField field) {
			if (!this.fieldPos.equals(field.fieldPos)) {
				return false;
			} else {
				return Objects.equals(this.figure, field.figure);
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("TTTServerField{");
		builder.append("fieldPos=").append(this.fieldPos).append(",");
		builder.append("figure=").append(this.figure == null ? "null" : this.figure).append("}");
		return builder.toString();
	}

}
