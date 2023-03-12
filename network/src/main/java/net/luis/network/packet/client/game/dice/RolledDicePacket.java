package net.luis.network.packet.client.game.dice;

import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.client.ClientPacket;
import net.luis.network.listener.PacketGetter;
import org.jetbrains.annotations.NotNull;

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
	
	public RolledDicePacket(@NotNull FriendlyByteBuffer buffer) {
		this.count = buffer.readInt();
	}
	
	@Override
	public void encode(@NotNull FriendlyByteBuffer buffer) {
		buffer.writeInt(this.count);
	}
	
	@PacketGetter
	public int getCount() {
		return this.count;
	}
	
}
