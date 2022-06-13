package net.vgc.network.packet.client.game;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;

public class GameActionFailedPacket implements ClientPacket {
	
	public GameActionFailedPacket() {
		
	}
	
	public GameActionFailedPacket(FriendlyByteBuffer buffer) {
		
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		
	}

	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleGameActionFailed();
	}

}
