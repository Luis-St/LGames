package net.vgc.games.wins4;

import java.util.List;

import com.google.common.collect.Lists;

import net.vgc.games.wins4.map.field.Wins4FieldPos;
import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.util.annotation.DecodingConstructor;

public class Wins4ResultLine implements Encodable {
	
	public static final Wins4ResultLine EMPTY = new Wins4ResultLine(Wins4FieldPos.NO, Wins4FieldPos.NO, Wins4FieldPos.NO, Wins4FieldPos.NO);
	
	private final Wins4FieldPos firstPos;
	private final Wins4FieldPos secondPos;
	private final Wins4FieldPos thirdPos;
	private final Wins4FieldPos fourthPos;
	
	public Wins4ResultLine(Wins4FieldPos firstPos, Wins4FieldPos secondPos, Wins4FieldPos thirdPos, Wins4FieldPos fourthPos) {
		this.firstPos = firstPos;
		this.secondPos = secondPos;
		this.thirdPos = thirdPos;
		this.fourthPos = fourthPos;
	}
	
	@DecodingConstructor
	private Wins4ResultLine(FriendlyByteBuffer buffer) {
		this.firstPos = buffer.read(Wins4FieldPos.class);
		this.secondPos = buffer.read(Wins4FieldPos.class);
		this.thirdPos = buffer.read(Wins4FieldPos.class);
		this.fourthPos = buffer.read(Wins4FieldPos.class);
	}
	
	public Wins4FieldPos getFirstPos() {
		return this.firstPos;
	}
	
	public Wins4FieldPos getSecondPos() {
		return this.secondPos;
	}
	
	public Wins4FieldPos getThirdPos() {
		return this.thirdPos;
	}
	
	public Wins4FieldPos getFourthPos() {
		return this.fourthPos;
	}
	
	public List<Wins4FieldPos> getPoses() {
		return Lists.newArrayList(this.firstPos, this.secondPos, this.thirdPos, this.fourthPos);
	}
	
	public boolean isOutOfMap() {
		return this.getPoses().stream().filter(Wins4FieldPos::isOutOfMap).findAny().isPresent();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.firstPos);
		buffer.write(this.secondPos);
		buffer.write(this.thirdPos);
		buffer.write(this.fourthPos);
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Wins4ResultLine resultLine) {
			if (!this.firstPos.equals(resultLine.firstPos)) {
				return false;
			} else if (!this.secondPos.equals(resultLine.secondPos)) {
				return false;
			} else if (!this.thirdPos.equals(resultLine.thirdPos)) {
				return false;
			} else {
				return this.fourthPos.equals(resultLine.fourthPos);
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Wins4ResultLine{");
		builder.append("firstPos=").append(this.firstPos).append(",");
		builder.append("secondPos=").append(this.secondPos).append(",");
		builder.append("thirdPos=").append(this.thirdPos).append(",");
		builder.append("fourthPos=").append(this.fourthPos).append("}");
		return builder.toString();
	}
	
}
