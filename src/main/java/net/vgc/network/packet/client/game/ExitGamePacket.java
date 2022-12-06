package net.vgc.network.packet.client.game;

import net.vgc.client.network.ClientPacketHandler;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;

/**
 *
 * @author Luis-st
 *
 */

public class ExitGamePacket implements ClientPacket {
	
	public ExitGamePacket() {
		
	}
	
	public ExitGamePacket(FriendlyByteBuffer buffer) {
		
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {

	}

	@Override
	public void handle(ClientPacketHandler handler) {
		handler.handleExitGame();
	}

}
