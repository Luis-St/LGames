package net.vgc.game.ludo.map.field;

import javax.annotation.Nullable;

import com.google.common.base.Objects;

import net.vgc.game.ludo.LudoType;
import net.vgc.game.ludo.player.LudoFigure;

public class LudoField {
	
	protected final LudoFieldType type;
	protected final LudoFieldPos pos;
	protected LudoFigure figure;
	
	public LudoField(LudoFieldType type, int green, int yellow, int blue, int red) {
		this(type, new LudoFieldPos(green, yellow, blue, red));
	}
	
	public LudoField(LudoFieldType type, LudoFieldPos pos) {
		this.type = type;
		this.pos = pos;
	}
	
	public LudoFieldType getType() {
		return this.type;
	}
	
	public LudoFieldPos getPos() {
		return this.pos;
	}
	
	public int getFieldForType(LudoType type) {
		return this.pos.getFieldForType(type);
	}
	
	public boolean isHome() {
		return this.type == LudoFieldType.HOME && this.pos.isHomeOrWin();
	}
	
	public boolean isStart() {
		return this.type == LudoFieldType.DEFAULT && this.pos.isStart();
	}
	
	public boolean isWin() {
		return this.type == LudoFieldType.WIN && this.pos.isHomeOrWin();
	}
	
	@Nullable
	public LudoFigure getFigure() {
		return this.figure;
	}
	
	public void setFigure(LudoFigure figure) {
		this.figure = figure;
	}
	
	public void clearField() {
		this.figure = null;
	}
	
	public boolean isEmpty() {
		return this.figure == null;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof LudoField field) {
			if (!this.type.equals(field.type)) {
				return false;
			} else if (this.pos.equals(field.pos)) {
				return false;
			} else {
				return Objects.equal(this.figure, field.figure);
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("LudoField{");
		builder.append("type=").append(this.type).append(",");
		builder.append("pos=").append(this.pos).append(",");
		builder.append("figure=").append(this.figure == null ? "null" : this.figure).append("}");
		return builder.toString();
	}
	
}
