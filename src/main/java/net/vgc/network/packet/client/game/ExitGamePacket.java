package net.vgc.network.packet.client.game;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.network.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;

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
