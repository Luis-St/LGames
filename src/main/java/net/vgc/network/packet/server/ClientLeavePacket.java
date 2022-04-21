package net.vgc.network.packet.server;

import java.util.UUID;

import net.vgc.network.FriendlyByteBuffer;
import net.vgc.network.packet.Packet;
import net.vgc.server.network.ServerPacketListener;
import net.vgc.util.Util;

public class ClientLeavePacket implements Packet<ServerPacketListener>, ServerPacket {
	
	protected final UUID uuid;
	
	public ClientLeavePacket(UUID uuid) {
		this.uuid = uuid == null ? Util.EMPTY_UUID : uuid;
	}
	
	public ClientLeavePacket(FriendlyByteBuffer buffer) {
		this.uuid = buffer.readUUID();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeUUID(this.uuid);
	}

	@Override
	public void handle(ServerPacketListener listener) {
		listener.handleClientLeave(this.uuid);
	}

}
