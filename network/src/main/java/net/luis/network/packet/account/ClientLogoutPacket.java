package net.luis.network.packet.account;

import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.listener.PacketGetter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class ClientLogoutPacket implements AccountPacket {
	
	private final String name;
	private final int id;
	private final UUID uuid;
	
	public ClientLogoutPacket(@NotNull String name, int id, @NotNull UUID uuid) {
		this.name = name;
		this.id = id;
		this.uuid = uuid;
	}
	
	public ClientLogoutPacket(@NotNull FriendlyByteBuffer buffer) {
		this.name = buffer.readString();
		this.id = buffer.readInt();
		this.uuid = buffer.readUUID();
	}
	
	@Override
	public void encode(@NotNull FriendlyByteBuffer buffer) {
		buffer.writeString(this.name);
		buffer.writeInt(this.id);
		buffer.writeUUID(this.uuid);
	}
	
	@PacketGetter
	public @NotNull String getName() {
		return this.name;
	}
	
	@PacketGetter
	public int getId() {
		return this.id;
	}
	
	@PacketGetter
	public @NotNull UUID getUUID() {
		return this.uuid;
	}
	
}
