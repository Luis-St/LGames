package net.vgc.network.packet;

import net.vgc.network.FriendlyByteBuffer;

public interface Packet<T extends PacketListener> {
	
	void encode(FriendlyByteBuffer buffer);
	
	void handle(T listener);
	
	default boolean skippable() {
		return false;
	}
	
}
