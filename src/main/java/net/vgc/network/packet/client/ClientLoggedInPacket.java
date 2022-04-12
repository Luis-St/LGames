package net.vgc.network.packet.client;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.common.LoginType;
import net.vgc.network.FriendlyByteBuffer;
import net.vgc.network.packet.Packet;
import net.vgc.server.account.PlayerAccountInfo;

public class ClientLoggedInPacket implements Packet<ClientPacketListener> {
	
	protected final LoginType loginType;
	protected final PlayerAccountInfo accountInfo;
	
	public ClientLoggedInPacket(LoginType loginType, PlayerAccountInfo accountInfo) {
		this.loginType = loginType;
		this.accountInfo = accountInfo;
	}
	
	public ClientLoggedInPacket(FriendlyByteBuffer buffer) {
		this.loginType = LoginType.fromName(buffer.readString());
		this.accountInfo = buffer.readAccountInfo();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeString(this.loginType.getName());
		buffer.writeAccountInfo(this.accountInfo);
	}

	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleClientLoggedIn(this.loginType, this.accountInfo);
	}

}
