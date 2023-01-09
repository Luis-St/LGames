package net.luis.network.packet.client;

import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.listener.PacketGetter;

/**
 *
 * @author Luis-st
 *
 */

public class ClientLoggedOutPacket implements ClientPacket {
	
	private final boolean successful;
	
	public ClientLoggedOutPacket(boolean successful) {
		this.successful = successful;
	}
	
	public ClientLoggedOutPacket(FriendlyByteBuffer buffer) {
		this.successful = buffer.readBoolean();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeBoolean(this.successful);
	}
	
	@PacketGetter
	public boolean isSuccessful() {
		return this.successful;
	}
	
}
