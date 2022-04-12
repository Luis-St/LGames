package net.vgc.network.packet.client;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.common.InfoResult;
import net.vgc.network.FriendlyByteBuffer;
import net.vgc.network.packet.Packet;

public class ClientLoggedOutPacket implements Packet<ClientPacketListener> {
	
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
