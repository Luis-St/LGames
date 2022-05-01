package net.vgc.network.packet.client;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.network.FriendlyByteBuffer;

public class ExitGamePacket implements ClientPacket {
	
	public ExitGamePacket() {
		
	}
	
	public ExitGamePacket(FriendlyByteBuffer buffer) {
		
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {

	}

	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleExitGame();
	}

}
