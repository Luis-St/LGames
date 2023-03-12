package net.luis.network.packet.client;

import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.listener.PacketGetter;
import org.jetbrains.annotations.NotNull;

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
	
	public ClientLoggedOutPacket(@NotNull FriendlyByteBuffer buffer) {
		this.successful = buffer.readBoolean();
	}
	
	@Override
	public void encode(@NotNull FriendlyByteBuffer buffer) {
		buffer.writeBoolean(this.successful);
	}
	
	@PacketGetter
	public boolean isSuccessful() {
		return this.successful;
	}
	
}
