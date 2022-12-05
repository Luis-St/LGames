package net.vgc.game.action.data.gobal;

import net.vgc.game.action.data.GameActionData;
import net.vgc.network.buffer.FriendlyByteBuffer;

/**
 *
 * @author Luis-st
 *
 */

public class IntegerData extends GameActionData {
	
	private final int value;
	
	public IntegerData(int value) {
		super();
		this.value = value;
	}
	
	public IntegerData(FriendlyByteBuffer buffer) {
		super(buffer);
		this.value = buffer.readInt();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeInt(this.value);
	}
	
	public int getValue() {
		return this.value;
	}
	
}
