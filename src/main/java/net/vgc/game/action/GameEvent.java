package net.vgc.game.action;

import net.vgc.network.FriendlyByteBuffer;

public interface GameEvent {
	
	void encode(FriendlyByteBuffer buffer);
	
	void handle();
	
}
