package net.vgc.network.packet.client.game;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;

public class CancelPlayAgainGameRequestPacket implements ClientPacket {
	
	public CancelPlayAgainGameRequestPacket() {
		
	}
	
	public CancelPlayAgainGameRequestPacket(FriendlyByteBuffer buffer) {
		
	}

	@Override
	public void encode(FriendlyByteBuffer buffer) {
		
	}

	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleCancelPlayAgainGameRequest();
	}
	
}
