package net.vgc.network.packet.server;

import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.listener.PacketGetter;

import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class ClientJoinPacket implements ServerPacket {
	
	private final String name;
	private final UUID uuid;
	
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
	
	@PacketGetter
	public String getName() {
		return this.name;
	}
	
	@PacketGetter
	public UUID getUUID() {
		return this.uuid;
	}
	
}
