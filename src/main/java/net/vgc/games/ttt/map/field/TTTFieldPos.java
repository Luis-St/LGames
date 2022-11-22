package net.vgc.games.ttt.map.field;

import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.player.GamePlayerType;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.util.Mth;
import net.vgc.util.annotation.DecodingConstructor;

public class TTTFieldPos implements GameFieldPos {
	
	public static final TTTFieldPos NO = new TTTFieldPos(-1, -1, -1);
	
	private final int position;
	private final int row;
	private final int column;
	
	public TTTFieldPos(int position, int row, int column) {
		this.position = position;
		this.row = row;
		this.column = column;
	}
	
	@DecodingConstructor
	public TTTFieldPos(FriendlyByteBuffer buffer) {
		this.position = buffer.readInt();
		this.row = buffer.readInt();
		this.column = buffer.readInt();
	}
	
	public static TTTFieldPos of(int position) {
		switch (position) {
			case 0:
				return new TTTFieldPos(0, 0, 0);
			case 1:
				return new TTTFieldPos(1, 0, 1);
			case 2:
				return new TTTFieldPos(2, 0, 2);
			case 3:
				return new TTTFieldPos(3, 1, 0);
			case 4:
				return new TTTFieldPos(4, 1, 1);
			case 5:
				return new TTTFieldPos(5, 1, 2);
			case 6:
				return new TTTFieldPos(6, 2, 0);
			case 7:
				return new TTTFieldPos(7, 2, 1);
			case 8:
				return new TTTFieldPos(8, 2, 2);
			default:
				return NO;
		}
	}
	
	@Override
	public int getPosition() {
		return this.position;
	}
	
	public int getRow() {
		return this.row;
	}
	
	public int getColumn() {
		return this.column;
	}
	
	@Override
	public int getPositionFor(GamePlayerType playerType) {
		return this.position;
	}
	
	@Override
	public boolean isStart() {
		return false;
	}
	
	@Override
	public boolean isOutOfMap() {
		if (Mth.isInBounds(this.position, 0, 8) && Mth.isInBounds(this.row, 0, 2) && Mth.isInBounds(this.column, 0, 2)) {
			return false;
		}
		return true;
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeInt(this.position);
		buffer.writeInt(this.row);
		buffer.writeInt(this.column);
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof TTTFieldPos pos) {
			if (this.position != pos.position) {
				return false;
			} else if (this.row != pos.row) {
				return false;
			} else {
				return this.column == pos.column;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("TTTFieldPos{");
		builder.append("position=").append(this.position).append(",");
		builder.append("row=").append(this.row).append(",");
		builder.append("column=").append(this.column).append("}");
		return builder.toString();
	}
	
}
