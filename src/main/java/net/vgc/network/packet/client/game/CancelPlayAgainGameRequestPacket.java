package net.vgc.network.packet.client.game;

import net.vgc.client.network.ClientPacketHandler;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;

/**
 *
 * @author Luis-st
 *
 */

public class CancelPlayAgainGameRequestPacket implements ClientPacket {
	
	public CancelPlayAgainGameRequestPacket() {
		
	}
	
	public CancelPlayAgainGameRequestPacket(FriendlyByteBuffer buffer) {
		
	}

	@Override
	public void encode(FriendlyByteBuffer buffer) {
		
	}

	@Override
	public void handle(ClientPacketHandler handler) {
		handler.handleCancelPlayAgainGameRequest();
	}
	
}
