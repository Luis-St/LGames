package net.vgc.network.packet.client.game.dice;

import net.vgc.client.network.ClientPacketHandler;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;

/**
 *
 * @author Luis-st
 *
 */

public class CancelRollDiceRequestPacket implements ClientPacket {
	
	public CancelRollDiceRequestPacket() {
		
	}
	
	public CancelRollDiceRequestPacket(FriendlyByteBuffer buffer) {
		
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		
	}
	
	@Override
	public void handle(ClientPacketHandler handler) {
		handler.handleCancelRollDiceRequest();
	}
	
}
