package net.vgc.game.action.data.gobal;

import net.vgc.game.action.data.GameActionData;
import net.vgc.network.buffer.FriendlyByteBuffer;

public class EmptyData extends GameActionData {
	
	public EmptyData() {
		super();
	}
	
	public EmptyData(FriendlyByteBuffer buffer) {
		super(buffer);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		
	}
	
}
