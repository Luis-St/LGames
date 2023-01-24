package net.luis.network.packet.account;

import net.luis.network.buffer.EncodableEnum;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.listener.PacketGetter;

/**
 *
 * @author Luis-st
 *
 */

public class ClientLoginPacket implements AccountPacket {
	
	private final EncodableEnum loginType;
	private final String name;
	private final int passwordHash;
	
	public ClientLoginPacket(Enum<?> loginType, String name, int passwordHash) {
		this.loginType = new EncodableEnum(loginType);
		this.name = name;
		this.passwordHash = passwordHash;
	}
	
	public ClientLoginPacket(FriendlyByteBuffer buffer) {
		this.loginType = buffer.read(EncodableEnum.class);
		this.name = buffer.readString();
		this.passwordHash = buffer.readInt();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.loginType);
		buffer.writeString(this.name);
		buffer.writeInt(this.passwordHash);
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
	public int getPasswordHash() {
		return this.passwordHash;
	}
	
}
