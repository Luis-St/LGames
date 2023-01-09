package net.luis.network.packet.account;

import net.luis.account.account.LoginType;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.listener.PacketGetter;

/**
 *
 * @author Luis-st
 *
 */

public class ClientLoginPacket implements AccountPacket {
	
	private final LoginType loginType;
	private final String name;
	private final int passwordHash;
	
	public ClientLoginPacket(LoginType loginType, String name, int passwordHash) {
		this.loginType = loginType;
		this.name = name;
		this.passwordHash = passwordHash;
	}
	
	public ClientLoginPacket(FriendlyByteBuffer buffer) {
		this.loginType = buffer.readEnum(LoginType.class);
		this.name = buffer.readString();
		this.passwordHash = buffer.readInt();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeEnum(this.loginType);
		buffer.writeString(this.name);
		buffer.writeInt(this.passwordHash);
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
	public int getPasswordHash() {
		return this.passwordHash;
	}
	
}
