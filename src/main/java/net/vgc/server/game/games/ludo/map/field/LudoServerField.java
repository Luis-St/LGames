package net.vgc.server.game.games.ludo.map.field;

import java.util.Objects;

import net.vgc.game.games.ludo.map.field.LudoFieldPos;
import net.vgc.game.games.ludo.map.field.LudoFieldType;
import net.vgc.game.player.field.GameFigure;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.server.game.games.ludo.player.figure.LudoServerFigure;
import net.vgc.server.game.map.field.ServerGameField;

public class LudoServerField implements ServerGameField, PacketHandler<ServerPacket> {
	
	protected final LudoFieldType fieldType;
	protected final LudoFieldPos fieldPos;
	protected LudoServerFigure figure;
	
	public LudoServerField(LudoFieldType fieldType, LudoFieldPos fieldPos) {
		this.fieldType = fieldType;
		this.fieldPos = fieldPos;
	}
	
	@Override
	public LudoFieldType getFieldType() {
		return this.fieldType;
	}

	@Override
	public LudoFieldPos getFieldPos() {
		return this.fieldPos;
	}

	@Override
	public boolean isHome() {
		return this.fieldType == LudoFieldType.HOME;
	}

	@Override
	public boolean isStart() {
		return this.fieldPos.isStart();
	}
	
	@Override
	public boolean isStartFor(GameFigure figure) {
		return figure.getStartPos().equals(this.fieldPos);
	}
	
	@Override
	public boolean isWin() {
		return this.fieldType == LudoFieldType.WIN;
	}

	@Override
	public LudoServerFigure getFigure() {
		return this.figure;
	}

	@Override
	public void setFigure(GameFigure figure) {
		this.figure = (LudoServerFigure) figure;
	}
	
	@Override
	public void handlePacket(ServerPacket serverPacket) {
		
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof LudoServerField field) {
			if (!this.fieldType.equals(field.fieldType)) {
				return false;
			} else if (!this.fieldPos.equals(field.fieldPos)) {
				return false;
			} else {
				return Objects.equals(this.figure, field.figure);
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("LudoServerField{");
		builder.append("fieldType=").append(this.fieldType).append(",");
		builder.append("fieldPos=").append(this.fieldPos).append(",");
		builder.append("figure=").append(this.figure == null ? "null" : this.figure).append("}");
		return builder.toString();
	}

}
