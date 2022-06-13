package net.vgc.network.packet.server;

import java.util.UUID;

import net.vgc.account.PlayerAccount;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.server.network.ServerPacketListener;
import net.vgc.util.Util;

public class ClientLeavePacket implements ServerPacket {
	
	protected final UUID uuid;
	
	public ClientLeavePacket(PlayerAccount account) {
		this.uuid = account == null ? Util.EMPTY_UUID : account.getUUID();
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
	
	public UUID getUUID() {
		return this.uuid;
	}
	
}
