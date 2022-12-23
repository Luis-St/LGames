package net.vgc.network.packet.account;

import net.vgc.account.PlayerAccount;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.listener.PacketGetter;

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
	
	@PacketGetter
	public PlayerAccount getAccount() {
		return this.account;
	}
	
}
