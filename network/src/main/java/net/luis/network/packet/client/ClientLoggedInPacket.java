package net.luis.network.packet.client;

import net.luis.network.buffer.EncodableEnum;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.listener.PacketGetter;

import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class ClientLoggedInPacket implements ClientPacket {
	
	private final EncodableEnum loginType;
	private final String name;
	private final int id;
	private final String mail;
	private final UUID uuid;
	
	public ClientLoggedInPacket(Enum<?> loginType, String name, int id, String mail, UUID uuid) {
		this.loginType = new EncodableEnum(loginType);
		this.name = name;
		this.id = id;
		this.mail = mail;
		this.uuid = uuid;
	}
	
	public ClientLoggedInPacket(FriendlyByteBuffer buffer) {
		this.loginType = buffer.read(EncodableEnum.class);
		this.name = buffer.readString();
		this.id = buffer.readInt();
		this.mail = buffer.readString();
		this.uuid = buffer.readUUID();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.loginType);
		buffer.writeString(this.name);
		buffer.writeInt(this.id);
		buffer.writeString(this.mail);
		buffer.writeUUID(this.uuid);
	}
	
	@PacketGetter
	public Enum<?> getLoginType() {
		return this.loginType.get();
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
