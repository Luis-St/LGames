package net.vgc.network.packet.client.game;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.network.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;

public class StopGamePacket implements ClientPacket {
	
	public StopGamePacket() {
		
	}
	
	public StopGamePacket(FriendlyByteBuffer buffer) {
		
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {

	}

	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleStopGame();
	}

}
