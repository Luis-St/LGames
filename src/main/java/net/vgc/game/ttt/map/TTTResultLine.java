package net.vgc.game.ttt.map;

import net.vgc.game.ttt.TTTType;

public class TTTResultLine {
	
	public static final TTTResultLine EMPTY = new TTTResultLine(TTTType.NO, -1, -1, -1, -1, -1, -1);
	
	protected final TTTType type;
	protected final int vMap0;
	protected final int hMap0;
	protected final int vMap1;
	protected final int hMap1;
	protected final int vMap2;
	protected final int hMap2;
	
	public TTTResultLine(TTTType type, int vMap0, int hMap0, int vMap1, int hMap1, int vMap2, int hMap2) {
		this.type = type;
		this.vMap0 = vMap0;
		this.hMap0 = hMap0;
		this.vMap1 = vMap1;
		this.hMap1 = hMap1;
		this.vMap2 = vMap2;
		this.hMap2 = hMap2;
	}
	
	public TTTType getType() {
		return this.type;
	}
	
	public int getVMap0() {
		return this.vMap0;
	}
	
	public int getHMap0() {
		return this.hMap0;
	}
	
	public int getVMap1() {
		return this.vMap1;
	}
	
	public int getHMap1() {
		return this.hMap1;
	}
	
	public int getVMap2() {
		return this.vMap2;
	}
	
	public int getHMap2() {
		return this.hMap2;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof TTTResultLine resultLine) {
			if (!this.type.equals(resultLine.type)) {
				return false;
			} else if (this.vMap0 != resultLine.vMap0) {
				return false;
			} else if (this.hMap0 != resultLine.hMap0) {
				return false;
			} else if (this.vMap1 != resultLine.vMap1) {
				return false;
			} else if (this.hMap1 != resultLine.hMap1) {
				return false;
			} else if (this.vMap2 != resultLine.vMap2) {
				return false;
			} else {
				return this.hMap2 == resultLine.hMap2;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("TTTResultLine{");
		builder.append("type=").append(this.type).append(",");
		builder.append("vMap0=").append(this.vMap0).append(",");
		builder.append("hMap0=").append(this.hMap0).append(",");
		builder.append("vMap1=").append(this.vMap1).append(",");
		builder.append("hMap1=").append(this.hMap1).append(",");
		builder.append("vMap2=").append(this.vMap2).append(",");
		builder.append("hMap2=").append(this.hMap2).append("}");
		return builder.toString();
	}
	
}
