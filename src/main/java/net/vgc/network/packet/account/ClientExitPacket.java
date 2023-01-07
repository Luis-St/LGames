package net.vgc.network.packet.account;

import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.listener.PacketGetter;

import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class ClientExitPacket implements AccountPacket {
	
	private final String name;
	private final int id;
	private final UUID uuid;
	
	public ClientExitPacket(String name, int id, UUID uuid) {
		this.name = name;
		this.id = id;
		this.uuid = uuid;
	}
	
	public ClientExitPacket(FriendlyByteBuffer buffer) {
		this.name = buffer.readString();
		this.id = buffer.readInt();
		this.uuid = buffer.readUUID();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeString(this.name);
		buffer.writeInt(this.id);
		buffer.writeUUID(this.uuid);
	}
	
	@PacketGetter
	public String getName() {
		return this.name;
	}
	
	@PacketGetter
	public int getId() {
		return this.id;
	}
	
	@PacketGetter
	public UUID getUUID() {
		return this.uuid;
	}
	
}
