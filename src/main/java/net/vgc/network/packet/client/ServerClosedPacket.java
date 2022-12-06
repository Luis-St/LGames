package net.vgc.network.packet.client;

import net.vgc.client.network.ClientPacketHandler;
import net.vgc.network.buffer.FriendlyByteBuffer;

/**
 *
 * @author Luis-st
 *
 */

public class ServerClosedPacket implements ClientPacket {
	
	public ServerClosedPacket() {
		
	}
	
	public ServerClosedPacket(FriendlyByteBuffer buffer) {
		
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		
	}
	
	@Override
	public void handle(ClientPacketHandler handler) {
		handler.handleServerClosed();
	}
	
}
