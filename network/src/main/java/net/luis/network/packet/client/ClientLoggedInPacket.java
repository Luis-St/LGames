package net.luis.network.packet.client;

import net.luis.network.buffer.EncodableEnum;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.listener.PacketGetter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class ClientLoggedInPacket implements ClientPacket {
	
	private final EncodableEnum loginType;
	private final String name;
	private final int id;
	private final String mail;
	private final UUID uuid;
	
	public ClientLoggedInPacket(@NotNull Enum<?> loginType, @NotNull String name, int id, @NotNull String mail, @NotNull UUID uuid) {
		this.loginType = new EncodableEnum(loginType);
		this.name = name;
		this.id = id;
		this.mail = mail;
		this.uuid = uuid;
	}
	
	public ClientLoggedInPacket(@NotNull FriendlyByteBuffer buffer) {
		this.loginType = buffer.read(EncodableEnum.class);
		this.name = buffer.readString();
		this.id = buffer.readInt();
		this.mail = buffer.readString();
		this.uuid = buffer.readUUID();
	}
	
	@Override
	public void encode(@NotNull FriendlyByteBuffer buffer) {
		buffer.write(this.loginType);
		buffer.writeString(this.name);
		buffer.writeInt(this.id);
		buffer.writeString(this.mail);
		buffer.writeUUID(this.uuid);
	}
	
	@PacketGetter
	public @NotNull Enum<?> getLoginType() {
		return this.loginType.get();
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
	public @NotNull String getMail() {
		return this.mail;
	}
	
	@PacketGetter
	public @NotNull UUID getUUID() {
		return this.uuid;
	}
	
}
