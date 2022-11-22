package net.vgc.network.packet.client;

import net.vgc.client.network.ClientPacketHandler;
import net.vgc.network.buffer.FriendlyByteBuffer;

public class ServerClosedPacket implements ClientPacket {
	
	public ServerClosedPacket() {
		
	}
	
	public ServerClosedPacket(FriendlyByteBuffer buffer) {
		
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		
	}
	
	@Override
	public void handle(ClientPacketHandler listener) {
		listener.handleServerClosed();
	}
	
}
