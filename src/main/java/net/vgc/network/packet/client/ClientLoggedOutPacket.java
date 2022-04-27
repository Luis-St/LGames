package net.vgc.network.packet.client;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.common.info.InfoResult;
import net.vgc.network.FriendlyByteBuffer;

public class ClientLoggedOutPacket implements ClientPacket {
	
	protected final InfoResult infoResult;
	
	public ClientLoggedOutPacket(InfoResult infoResult) {
		this.infoResult = infoResult;
	}
	
	public ClientLoggedOutPacket(FriendlyByteBuffer buffer) {
		this.infoResult = buffer.readInfoResult();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeInfoResult(this.infoResult);
	}

	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleClientLoggedOut(this.infoResult);
	}

}
