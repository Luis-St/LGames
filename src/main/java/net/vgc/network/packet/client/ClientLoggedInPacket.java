package net.vgc.network.packet.client;

import net.vgc.account.LoginType;
import net.vgc.account.PlayerAccount;
import net.vgc.client.network.ClientPacketHandler;
import net.vgc.network.buffer.FriendlyByteBuffer;

/**
 *
 * @author Luis-st
 *
 */

public class ClientLoggedInPacket implements ClientPacket {
	
	private final LoginType loginType;
	private final PlayerAccount account;
	private final boolean successful;
	
	public ClientLoggedInPacket(LoginType loginType, PlayerAccount account, boolean successful) {
		this.loginType = loginType;
		this.account = account;
		this.successful = successful;
	}
	
	public ClientLoggedInPacket(FriendlyByteBuffer buffer) {
		this.loginType = buffer.readEnum(LoginType.class);
		this.account = buffer.read(PlayerAccount.class);
		this.successful = buffer.readBoolean();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeEnum(this.loginType);
		buffer.write(this.account);
		buffer.writeBoolean(this.successful);
	}
	
	@Override
	public void handle(ClientPacketHandler listener) {
		listener.handleClientLoggedIn(this.loginType, this.account, this.successful);
	}
	
	public LoginType getLoginType() {
		return this.loginType;
	}
	
	public PlayerAccount getAccount() {
		return this.account;
	}
	
	public boolean isSuccessful() {
		return this.successful;
	}
	
}
