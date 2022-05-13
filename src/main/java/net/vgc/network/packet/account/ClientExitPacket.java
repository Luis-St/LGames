package net.vgc.network.packet.account;

import net.vgc.account.PlayerAccount;
import net.vgc.account.network.AccountServerPacketListener;
import net.vgc.network.buffer.FriendlyByteBuffer;

public class ClientExitPacket implements AccountPacket {
	
	protected final PlayerAccount account;
	
	public ClientExitPacket(PlayerAccount account) {
		this.account = account == null ? PlayerAccount.UNKNOWN : account;
	}
	
	public ClientExitPacket(FriendlyByteBuffer buffer) {
		this.account = buffer.readAccount();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeAccount(this.account);
	}

	@Override
	public void handle(AccountServerPacketListener listener) {
		listener.handleClientLogoutExit(this.account.getName(), this.account.getPassword());
	}
	
	public PlayerAccount getAccount() {
		return this.account;
	}

}
