package net.vgc.game.games.ttt;

import java.util.List;

import com.google.common.collect.Lists;

import net.vgc.game.games.ttt.map.field.TTTFieldPos;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.util.annotation.DecodingConstructor;

public class TTTResultLine implements Encodable {
	
	public static final TTTResultLine EMPTY = new TTTResultLine(TTTFieldPos.NO, TTTFieldPos.NO, TTTFieldPos.NO);
	
	protected final TTTFieldPos firstPos;
	protected final TTTFieldPos secondPos;
	protected final TTTFieldPos thirdPos;
	
	public TTTResultLine(TTTFieldPos firstPos, TTTFieldPos secondPos, TTTFieldPos thirdPos) {
		this.firstPos = firstPos;
		this.secondPos = secondPos;
		this.thirdPos = thirdPos;
	}
	
	@DecodingConstructor
	private TTTResultLine(FriendlyByteBuffer buffer) {
		this.firstPos = (TTTFieldPos) GameFieldPos.decode(buffer);
		this.secondPos = (TTTFieldPos) GameFieldPos.decode(buffer);
		this.thirdPos = (TTTFieldPos) GameFieldPos.decode(buffer);
	}
	
	public TTTFieldPos getFirstPos() {
		return this.firstPos;
	}
	
	public TTTFieldPos getSecondPos() {
		return this.secondPos;
	}
	
	public TTTFieldPos getThirdPos() {
		return this.thirdPos;
	}
	
	public List<TTTFieldPos> getPoses() {
		return Lists.newArrayList(this.firstPos, this.secondPos, this.thirdPos);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.firstPos);
		buffer.write(this.secondPos);
		buffer.write(this.thirdPos);
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof TTTResultLine resultLine) {
			if (!this.firstPos.equals(resultLine.firstPos)) {
				return false;
			} else if (!this.secondPos.equals(resultLine.secondPos)) {
				return false;
			} else {
				return this.thirdPos.equals(resultLine.thirdPos);
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("TTTResultLine{");
		builder.append("firstPos=").append(this.firstPos).append(",");
		builder.append("secondPos=").append(this.secondPos).append(",");
		builder.append("thirdPos=").append(this.thirdPos).append("}");
		return builder.toString();
	}
	
}
