package net.vgc.network.packet.account;

import net.vgc.account.PlayerAccount;
import net.vgc.account.network.AccountServerPacketListener;
import net.vgc.network.FriendlyByteBuffer;

public class ClientLogoutPacket implements AccountPacket {
	
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
		listener.handleClientLogout(this.account.getName(), this.account.getPassword());
	}

}
