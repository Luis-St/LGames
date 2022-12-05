package net.vgc.network.packet.account;

import net.vgc.account.PlayerAccount;
import net.vgc.account.network.AccountServerPacketHandler;
import net.vgc.network.buffer.FriendlyByteBuffer;

/**
 *
 * @author Luis-st
 *
 */

public class ClientExitPacket implements AccountPacket {
	
	private final PlayerAccount account;
	
	public ClientExitPacket(PlayerAccount account) {
		this.account = account == null ? PlayerAccount.UNKNOWN : account;
	}
	
	public ClientExitPacket(FriendlyByteBuffer buffer) {
		this.account = buffer.read(PlayerAccount.class);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.account);
	}
	
	@Override
	public void handle(AccountServerPacketHandler listener) {
		listener.handleClientLogoutExit(this.account.getName(), this.account.getPassword());
	}
	
	public PlayerAccount getAccount() {
		return this.account;
	}
	
}
