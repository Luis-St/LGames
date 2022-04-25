package net.vgc.network.packet.server;

import java.util.UUID;

import net.vgc.account.PlayerAccount;
import net.vgc.network.FriendlyByteBuffer;
import net.vgc.server.network.ServerPacketListener;
import net.vgc.util.Util;

public class ClientLeavePacket implements ServerPacket {
	
	protected final UUID uuid;
	
	public ClientLeavePacket(PlayerAccount account) {
		this(account == null ? Util.EMPTY_UUID : account.getUUID());
	}
	
	public ClientLeavePacket(UUID uuid) {
		this.uuid = Util.EMPTY_UUID;
	}
	
	public ClientLeavePacket(FriendlyByteBuffer buffer) {
		this.uuid = buffer.readUUID();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeUUID(this.uuid);
	}

	@Override
	public void handle(ServerPacketListener listener) {
		listener.handleClientLeave(this.uuid);
	}

}
