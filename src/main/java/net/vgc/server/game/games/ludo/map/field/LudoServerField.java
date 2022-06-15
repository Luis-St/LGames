package net.vgc.server.game.games.ludo.map.field;

import java.util.Objects;

import net.vgc.game.games.ludo.map.field.LudoFieldPos;
import net.vgc.game.games.ludo.map.field.LudoFieldType;
import net.vgc.game.games.ludo.player.LudoPlayerType;
import net.vgc.game.map.field.GameFieldInfo;
import net.vgc.game.player.field.GameFigure;
import net.vgc.network.packet.PacketHandler;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.player.GameProfile;
import net.vgc.server.game.games.ludo.player.figure.LudoServerFigure;
import net.vgc.server.game.map.field.ServerGameField;
import net.vgc.util.Util;

public class LudoServerField implements ServerGameField, PacketHandler<ServerPacket> {
	
	protected final LudoFieldType fieldType;
	protected final LudoPlayerType colorType;
	protected final LudoFieldPos fieldPos;
	protected LudoServerFigure figure;
	
	public LudoServerField(LudoFieldType fieldType, LudoPlayerType colorType, LudoFieldPos fieldPos) {
		this.fieldType = fieldType;
		this.colorType = colorType;
		this.fieldPos = fieldPos;
	}
	
	@Override
	public LudoFieldType getFieldType() {
		return this.fieldType;
	}
	
	public LudoPlayerType getColorType() {
		return this.colorType;
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
	public GameFieldInfo getFieldInfo() {
		if (this.isEmpty()) {
			return new GameFieldInfo(this.getFieldType(), this.getColorType(), this.fieldPos, GameProfile.EMPTY, -1, Util.EMPTY_UUID);
		}
		LudoServerFigure figure = this.getFigure();
		return new GameFieldInfo(this.getFieldType(), this.getColorType(), this.fieldPos, figure.getPlayer().getPlayer().getProfile(), figure.getCount(), figure.getUUID());
	}
	
	@Override
	public void handlePacket(ServerPacket serverPacket) {
		
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof LudoServerField field) {
			if (!this.fieldType.equals(field.fieldType)) {
				return false;
			} else if (!this.colorType.equals(field.colorType)) {
				return false;
			}  else if (!this.fieldPos.equals(field.fieldPos)) {
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
		builder.append("colorType=").append(this.colorType).append(",");
		builder.append("fieldPos=").append(this.fieldPos).append(",");
		builder.append("figure=").append(this.figure == null ? "null" : this.figure).append("}");
		return builder.toString();
	}

}
