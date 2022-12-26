package net.vgc.network.packet.account;

import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.listener.PacketGetter;

/**
 *
 * @author Luis-st
 *
 */

public class ClientExitPacket implements AccountPacket {
	
	private final String name;
	private final int id;
	private final int passwordHash;
	
	public ClientExitPacket(String name, int id, int passwordHash) {
		this.name = name;
		this.id = id;
		this.passwordHash = passwordHash;
	}
	
	public ClientExitPacket(FriendlyByteBuffer buffer) {
		this.name = buffer.readString();
		this.id = buffer.readInt();
		this.passwordHash = buffer.readInt();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeString(this.name);
		buffer.writeInt(this.id);
		buffer.writeString(this.name);
		buffer.writeInt(this.passwordHash);
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
	public int getPasswordHash() {
		return this.passwordHash;
	}
	
}
