package net.luis.ttt.map.field;

import net.luis.utils.math.Mth;
import net.luis.utils.util.ToString;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.player.GamePlayerType;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.util.annotation.DecodingConstructor;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

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
		return switch (position) {
			case 0 -> new TTTFieldPos(0, 0, 0);
			case 1 -> new TTTFieldPos(1, 0, 1);
			case 2 -> new TTTFieldPos(2, 0, 2);
			case 3 -> new TTTFieldPos(3, 1, 0);
			case 4 -> new TTTFieldPos(4, 1, 1);
			case 5 -> new TTTFieldPos(5, 1, 2);
			case 6 -> new TTTFieldPos(6, 2, 0);
			case 7 -> new TTTFieldPos(7, 2, 1);
			case 8 -> new TTTFieldPos(8, 2, 2);
			default -> NO;
		};
	}
	
	@Override
	public int getPosition() {
		return this.position;
	}
	
	@Override
	public int getRow() {
		return this.row;
	}
	
	@Override
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
		return !Mth.isInBounds(this.position, 0, 8) || !Mth.isInBounds(this.row, 0, 2) || !Mth.isInBounds(this.column, 0, 2);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeInt(this.position);
		buffer.writeInt(this.row);
		buffer.writeInt(this.column);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TTTFieldPos that)) return false;
		
		if (this.position != that.position) return false;
		if (this.row != that.row) return false;
		return this.column == that.column;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.position, this.row, this.column);
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
}
