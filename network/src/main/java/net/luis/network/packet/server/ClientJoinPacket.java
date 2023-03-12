package net.luis.network.packet.server;

import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.listener.PacketGetter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class ClientJoinPacket implements ServerPacket {
	
	private final String name;
	private final UUID uuid;
	
	public ClientJoinPacket(@NotNull String name, @NotNull UUID uuid) {
		this.name = name;
		this.uuid = uuid;
	}
	
	public ClientJoinPacket(@NotNull FriendlyByteBuffer buffer) {
		this.name = buffer.readString();
		this.uuid = buffer.readUUID();
	}
	
	@Override
	public void encode(@NotNull FriendlyByteBuffer buffer) {
		buffer.writeString(this.name);
		buffer.writeUUID(this.uuid);
	}
	
	@PacketGetter
	public @NotNull String getName() {
		return this.name;
	}
	
	@PacketGetter
	public @NotNull UUID getUUID() {
		return this.uuid;
	}
	
}
