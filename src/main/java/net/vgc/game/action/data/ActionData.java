package net.vgc.game.action.data;

import net.vgc.network.buffer.FriendlyByteBuffer;

public abstract class ActionData {
	
	protected ActionData() {
		
	}
	
	protected ActionData(FriendlyByteBuffer buffer) {
		
	}
	
	public abstract void encode(FriendlyByteBuffer buffer);
	
}
