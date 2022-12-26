package net.vgc.network.packet.client;

import java.util.UUID;

import net.vgc.account.account.LoginType;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.listener.PacketGetter;

/**
 *
 * @author Luis-st
 *
 */

public class ClientLoggedInPacket implements ClientPacket {
	
	private final LoginType loginType;
	private final String name;
	private final int id;
	private final String mail;
	private final UUID uuid;
	
	public ClientLoggedInPacket(LoginType loginType, String name, int id, String mail, UUID uuid) {
		this.loginType = loginType;
		this.name = name;
		this.id = id;
		this.mail = mail;
		this.uuid = uuid;
	}
	
	public ClientLoggedInPacket(FriendlyByteBuffer buffer) {
		this.loginType = buffer.readEnum(LoginType.class);
		this.name = buffer.readString();
		this.id = buffer.readInt();
		this.mail = buffer.readString();
		this.uuid = buffer.readUUID();
	}

	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeEnum(this.loginType);
		buffer.writeString(this.name);
		buffer.writeInt(this.id);
		buffer.writeString(this.mail);
		buffer.writeUUID(this.uuid);
	}
	
	@PacketGetter
	public LoginType getLoginType() {
		return this.loginType;
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
	public String getMail() {
		return this.mail;
	}
	
	@PacketGetter
	public UUID getUUID() {
		return this.uuid;
	}
	
}
