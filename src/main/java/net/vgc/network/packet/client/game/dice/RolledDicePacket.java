package net.vgc.network.packet.client.game.dice;

import net.vgc.client.network.ClientPacketHandler;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;

/**
 *
 * @author Luis-st
 *
 */


public class RolledDicePacket implements ClientPacket {
	
	private final int count;
	
	public RolledDicePacket(int count) {
		this.count = count;
	}
	
	public RolledDicePacket(FriendlyByteBuffer buffer) {
		this.count = buffer.readInt();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeInt(this.count);
	}

	@Override
	public void handle(ClientPacketHandler handler) {
		handler.handleRolledDice(this.count);
	}
	
	public int getCount() {
		return this.count;
	}
	
}
