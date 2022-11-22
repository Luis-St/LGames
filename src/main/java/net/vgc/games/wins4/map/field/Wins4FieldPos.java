package net.vgc.games.wins4.map.field;

import java.util.List;

import com.google.common.collect.Lists;

import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.player.GamePlayerType;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.util.Mth;
import net.vgc.util.Util;
import net.vgc.util.annotation.DecodingConstructor;

public class Wins4FieldPos implements GameFieldPos {
	
	public static final Wins4FieldPos NO = new Wins4FieldPos(-1, -1, -1);
	private static final List<List<Integer>> ROW_COLUMN_GRID = Util.make(Lists.newArrayList(), (list) -> {
		list.add(Lists.newArrayList(0, 1, 2, 3, 4, 5, 6));
		list.add(Lists.newArrayList(7, 8, 9, 10, 11, 12, 13));
		list.add(Lists.newArrayList(14, 15, 16, 17, 18, 19, 20));
		list.add(Lists.newArrayList(21, 22, 23, 24, 25, 26, 27));
		list.add(Lists.newArrayList(28, 29, 30, 31, 32, 33, 34));
		list.add(Lists.newArrayList(35, 36, 37, 38, 39, 40, 41));
	});
	private final int position;
	private final int row;
	private final int column;
	
	public Wins4FieldPos(int position, int row, int column) {
		this.position = position;
		this.row = row;
		this.column = column;
	}
	
	@DecodingConstructor
	public Wins4FieldPos(FriendlyByteBuffer buffer) {
		this.position = buffer.readInt();
		this.row = buffer.readInt();
		this.column = buffer.readInt();
	}
	
	public static Wins4FieldPos of(int position) {
		int row = -1;
		int column = -1;
		for (int i = 0; i < ROW_COLUMN_GRID.size(); i++) {
			if (ROW_COLUMN_GRID.get(i).contains(position)) {
				row = i;
				column = ROW_COLUMN_GRID.get(i).indexOf(position);
				break;
			}
		}
		if (row != -1 && column != -1) {
			return new Wins4FieldPos(position, row, column);
		} else {
			return NO;
		}
	}
	
	public static Wins4FieldPos of(int row, int column) {
		if (Mth.isInBounds(row, 0, 5) && Mth.isInBounds(column, 0, 6)) {
			return new Wins4FieldPos(ROW_COLUMN_GRID.get(row).get(column), row, column);
		}
		return NO;
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
		if (Mth.isInBounds(this.position, 0, 41) && Mth.isInBounds(this.row, 0, 5) && Mth.isInBounds(this.column, 0, 6)) {
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
		if (object instanceof Wins4FieldPos pos) {
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
		StringBuilder builder = new StringBuilder("Win4FieldPos{");
		builder.append("position=").append(this.position).append(",");
		builder.append("row=").append(this.row).append(",");
		builder.append("column=").append(this.column).append("}");
		return builder.toString();
	}
	
}
