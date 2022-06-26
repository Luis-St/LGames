package net.vgc.network.packet.client.game;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;

public class CanSelectGameFieldPacket implements ClientPacket {
	
	public CanSelectGameFieldPacket() {
		
	}
	
	public CanSelectGameFieldPacket(FriendlyByteBuffer buffer) {
		
	}

	@Override
	public void encode(FriendlyByteBuffer buffer) {
		
	}

	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleCanSelectGameField();
	}
	
}
