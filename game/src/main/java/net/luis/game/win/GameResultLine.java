package net.luis.game.win;

import com.google.common.collect.Lists;
import net.luis.game.map.field.GameFieldPos;
import net.luis.network.buffer.Encodable;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.annotation.DecodingConstructor;
import net.luis.utils.util.ToString;

import java.util.List;
import java.util.Objects;

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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GameResultLine that)) return false;
		
		return this.fieldPositions.equals(that.fieldPositions);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.fieldPositions);
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
}
