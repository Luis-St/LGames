package net.vgc.network.packet.client;

import net.vgc.client.network.ClientPacketHandler;
import net.vgc.network.buffer.FriendlyByteBuffer;

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
	
	@Override
	public void handle(ClientPacketHandler listener) {
		listener.handleClientLoggedOut(this.successful);
	}
	
	public boolean isSuccessful() {
		return this.successful;
	}
	
}
