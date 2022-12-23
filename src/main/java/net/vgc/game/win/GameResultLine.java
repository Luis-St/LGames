package net.vgc.game.win;

import java.util.List;

import com.google.common.collect.Lists;

import net.luis.utils.util.ToString;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.util.annotation.DecodingConstructor;

/**
 *
 * @author Luis-st
 *
 */

public class GameResultLine implements Encodable {
	
	public static final GameResultLine EMPTY = new GameResultLine(Lists.newArrayList());
	
	private final List<GameFieldPos> fieldPositions;
	
	public GameResultLine(GameFieldPos... fieldPositions) {
		this(Lists.newArrayList(fieldPositions));
	}
	
	public GameResultLine(List<GameFieldPos> fieldPositions) {
		this.fieldPositions = fieldPositions;
	}
	
	@DecodingConstructor
	private GameResultLine(FriendlyByteBuffer buffer) {
		this.fieldPositions = buffer.readList(buffer::readInterface);
	}
	
	public int getLineSize() {
		return this.fieldPositions.size();
	}
	
	public GameFieldPos getPos(int position) {
		return this.fieldPositions.get(position);
	}
	
	public List<GameFieldPos> getPositions() {
		return this.fieldPositions;
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeList(this.fieldPositions, buffer::writeInterface);
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof GameResultLine resultLine) {
			return this.fieldPositions.equals(resultLine.fieldPositions);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
}
