package net.vgc.network.packet.server;

import java.util.UUID;

import net.vgc.account.PlayerAccount;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.listener.PacketGetter;
import net.vgc.server.network.ServerPacketHandler;
import net.vgc.util.Util;

/**
 *
 * @author Luis-st
 *
 */

public class ClientLeavePacket implements ServerPacket {
	
	private final UUID uuid;
	
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
	public void handle(ServerPacketHandler handler) {
		handler.handleClientLeave(this.uuid);
	}
	
	@PacketGetter
	public UUID getUUID() {
		return this.uuid;
	}
	
}
