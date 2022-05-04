package net.vgc.network.packet.client;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.network.FriendlyByteBuffer;

public class ClientLoggedOutPacket implements ClientPacket {
	
	protected final boolean successful;
	
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
	public void handle(ClientPacketListener listener) {
		listener.handleClientLoggedOut(this.successful);
	}
	
	public boolean isSuccessful() {
		return this.successful;
	}

}
