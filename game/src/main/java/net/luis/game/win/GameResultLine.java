package net.luis.game.win;

import com.google.common.collect.Lists;
import net.luis.game.map.field.GameFieldPos;
import net.luis.netcore.buffer.Encodable;
import net.luis.netcore.buffer.FriendlyByteBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	
	public GameResultLine(@NotNull GameFieldPos... fieldPositions) {
		this(Lists.newArrayList(fieldPositions));
	}
	
	public GameResultLine(@NotNull List<GameFieldPos> fieldPositions) {
		this.fieldPositions = fieldPositions;
	}
	
	public GameResultLine(@NotNull FriendlyByteBuffer buffer) {
		this.fieldPositions = buffer.readList(buffer::readInterface);
	}
	
	public int getLineSize() {
		return this.fieldPositions.size();
	}
	
	public @NotNull GameFieldPos getPos(int position) {
		return this.fieldPositions.get(position);
	}
	
	public @NotNull List<GameFieldPos> getPositions() {
		return this.fieldPositions;
	}
	
	@Override
	public void encode(@NotNull FriendlyByteBuffer buffer) {
		buffer.writeList(this.fieldPositions, buffer::writeInterface);
	}
	
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (!(o instanceof GameResultLine that)) return false;
		
		return this.fieldPositions.equals(that.fieldPositions);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.fieldPositions);
	}
	
	@Override
	public @NotNull String toString() {
		return ToString.toString(this);
	}
	
}
