package net.vgc.network.packet.account;

import net.vgc.account.LoginType;
import net.vgc.account.network.AccountServerPacketHandler;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.listener.PacketGetter;

/**
 *
 * @author Luis-st
 *
 */

public class ClientLoginPacket implements AccountPacket {
	
	private final LoginType loginType;
	private final String name;
	private final String password;
	
	public ClientLoginPacket(LoginType loginType, String name, String password) {
		this.loginType = loginType;
		this.name = name;
		this.password = password;
	}
	
	public ClientLoginPacket(FriendlyByteBuffer buffer) {
		this.loginType = buffer.readEnum(LoginType.class);
		this.name = buffer.readString();
		this.password = buffer.readString();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeEnum(this.loginType);
		buffer.writeString(this.name);
		buffer.writeString(this.password);
	}
	
	@Override
	public void handle(AccountServerPacketHandler handler) {
		handler.handleClientLogin(this.loginType, this.name, this.password);
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
	public String getPassword() {
		return this.password;
	}
	
}
