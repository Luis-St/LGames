package net.vgc.network.packet.client;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.network.FriendlyByteBuffer;

public class ServerClosedPacket implements ClientPacket {
	
	public ServerClosedPacket() {
		
	}
	
	public ServerClosedPacket(FriendlyByteBuffer buffer) {
		
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {

	}

	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleServerClosed();
	}

}
