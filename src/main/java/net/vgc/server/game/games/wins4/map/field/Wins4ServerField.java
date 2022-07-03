package net.vgc.server.game.games.wins4.map.field;

import java.util.Objects;

import net.vgc.game.games.wins4.map.field.Wins4FieldPos;
import net.vgc.game.games.wins4.map.field.Wins4FieldType;
import net.vgc.game.games.wins4.player.Wins4PlayerType;
import net.vgc.game.map.field.GameFieldInfo;
import net.vgc.game.player.field.GameFigure;
import net.vgc.player.GameProfile;
import net.vgc.server.game.games.wins4.player.figure.Wins4ServerFigure;
import net.vgc.server.game.map.field.ServerGameField;
import net.vgc.util.Util;

public class Wins4ServerField implements ServerGameField {
	
	protected final Wins4FieldPos fieldPos;
	protected Wins4ServerFigure figure;
	
	public Wins4ServerField(Wins4FieldPos fieldPos) {
		this.fieldPos = fieldPos;
	}

	@Override
	public Wins4FieldType getFieldType() {
		LOGGER.warn("Fail to get field type of field {}, since 4 wins fields does not have a field type", this.getFieldPos().getPosition());
		return Wins4FieldType.DEFAULT;
	}

	@Override
	public Wins4FieldPos getFieldPos() {
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
	public Wins4ServerFigure getFigure() {
		return this.figure;
	}

	@Override
	public void setFigure(GameFigure figure) {
		this.figure = (Wins4ServerFigure) figure;
	}
	
	@Override
	public GameFieldInfo getFieldInfo() {
		if (this.isEmpty()) {
			return new GameFieldInfo(Wins4FieldType.DEFAULT, Wins4PlayerType.NO, this.fieldPos, GameProfile.EMPTY, -1, Util.EMPTY_UUID);
		}
		Wins4ServerFigure figure = this.getFigure();
		return new GameFieldInfo(Wins4FieldType.DEFAULT, Wins4PlayerType.NO, this.fieldPos, figure.getPlayer().getPlayer().getProfile(), figure.getCount(), figure.getUUID());
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Wins4ServerField field) {
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
		StringBuilder builder = new StringBuilder("Win4ServerField{");
		builder.append("fieldPos=").append(this.fieldPos).append(",");
		builder.append("figure=").append(this.figure == null ? "null" : this.figure).append("}");
		return builder.toString();
	}
	
}
