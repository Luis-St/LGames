package net.vgc.network.packet.server;

import java.util.UUID;

import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.listener.PacketGetter;

/**
 *
 * @author Luis-st
 *
 */

public class ClientLeavePacket implements ServerPacket {
	
	private final String name;
	private final UUID uuid;
	
	public ClientLeavePacket(String name, UUID uuid) {
		this.name = name;
		this.uuid = uuid;
	}
	
	public ClientLeavePacket(FriendlyByteBuffer buffer) {
		this.name = buffer.readString();
		this.uuid = buffer.readUUID();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeString(this.name);
		buffer.writeUUID(this.uuid);
	}
	
	@PacketGetter
	public String getName() {
		return this.name;
	}
	
	@PacketGetter
	public UUID getUUID() {
		return this.uuid;
	}
	
}

