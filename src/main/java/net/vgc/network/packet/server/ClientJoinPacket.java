package net.vgc.network.packet.server;

import java.util.UUID;

import net.vgc.network.FriendlyByteBuffer;
import net.vgc.server.network.ServerPacketListener;

public class ClientJoinPacket implements ServerPacket {
	
	protected final String name;
	protected final UUID uuid;
	
	public ClientJoinPacket(String name, UUID uuid) {
		this.name = name;
		this.uuid = uuid;
	}
	
	public ClientJoinPacket(FriendlyByteBuffer buffer) {
		this.name = buffer.readString();
		this.uuid = buffer.readUUID();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeString(this.name);
		buffer.writeUUID(this.uuid);
	}

	@Override
	public void handle(ServerPacketListener listener) {
		listener.handleClientJoin(this.name, this.uuid);
	}

}
