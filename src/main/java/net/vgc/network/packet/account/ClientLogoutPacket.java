package net.vgc.network.packet.account;

import net.vgc.network.FriendlyByteBuffer;
import net.vgc.network.packet.Packet;
import net.vgc.server.account.PlayerAccount;
import net.vgc.server.account.network.AccountServerPacketListener;

public class ClientLogoutPacket implements Packet<AccountServerPacketListener> {
	
	protected final PlayerAccount account;
	
	public ClientLogoutPacket(PlayerAccount account) {
		this.account = account;
	}
	
	public ClientLogoutPacket(FriendlyByteBuffer buffer) {
		this.account = buffer.readAccount();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeAccount(this.account);
	}

	@Override
	public void handle(AccountServerPacketListener listener) {
		listener.handleClientLogout(this.account);
	}

}
