package net.vgc.network.packet.account;

import net.vgc.account.PlayerAccount;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.listener.PacketGetter;

/**
 *
 * @author Luis-st
 *
 */

public class ClientLogoutPacket implements AccountPacket {
	
	private final PlayerAccount account;
	
	public ClientLogoutPacket(PlayerAccount account) {
		this.account = account;
	}
	
	public ClientLogoutPacket(FriendlyByteBuffer buffer) {
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
