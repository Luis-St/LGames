package net.vgc.server.game.games.ttt.map.field;

import java.util.Objects;

import net.vgc.game.games.ttt.map.field.TTTFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.game.player.field.GameFigure;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.server.game.games.ttt.player.figure.TTTServerFigure;
import net.vgc.server.game.map.field.ServerGameField;

public class TTTServerField implements ServerGameField {
	
	protected final TTTFieldPos fieldPos;
	protected TTTServerFigure figure;
	
	public TTTServerField(TTTFieldPos fieldPos) {
		this.fieldPos = fieldPos;
	}
	
	@Override
	public GameFieldType getFieldType() {
		LOGGER.warn("Fail to get field type of field {}, since tic tac toe fields does not have a field type", this.getFieldPos().getPosition());
		return null;
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
	public void handlePacket(ServerPacket packet) {
		
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
