package net.vgc.network.packet.client.game.dice;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;

public class CanRollDiceAgainPacket implements ClientPacket {
	
	public CanRollDiceAgainPacket() {
		
	}
	
	public CanRollDiceAgainPacket(FriendlyByteBuffer buffer) {
		
	}

	@Override
	public void encode(FriendlyByteBuffer buffer) {
		
	}

	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleCanRollDiceAgain();
	}
	
}
