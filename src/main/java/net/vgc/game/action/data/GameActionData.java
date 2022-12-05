package net.vgc.game.action.data;

import net.vgc.network.buffer.FriendlyByteBuffer;

/**
 *
 * @author Luis-st
 *
 */

public abstract class GameActionData {
	
	protected GameActionData() {
		
	}
	
	protected GameActionData(FriendlyByteBuffer buffer) {
		
	}
	
	public abstract void encode(FriendlyByteBuffer buffer);
	
}
