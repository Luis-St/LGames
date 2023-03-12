package net.luis.network.packet.account;

import net.luis.network.buffer.EncodableEnum;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.listener.PacketGetter;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public class ClientLoginPacket implements AccountPacket {
	
	private final EncodableEnum loginType;
	private final String name;
	private final int passwordHash;
	
	public ClientLoginPacket(@NotNull Enum<?> loginType, @NotNull String name, int passwordHash) {
		this.loginType = new EncodableEnum(loginType);
		this.name = name;
		this.passwordHash = passwordHash;
	}
	
	public ClientLoginPacket(@NotNull FriendlyByteBuffer buffer) {
		this.loginType = buffer.read(EncodableEnum.class);
		this.name = buffer.readString();
		this.passwordHash = buffer.readInt();
	}
	
	@Override
	public void encode(@NotNull FriendlyByteBuffer buffer) {
		buffer.write(this.loginType);
		buffer.writeString(this.name);
		buffer.writeInt(this.passwordHash);
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
	public int getPasswordHash() {
		return this.passwordHash;
	}
	
}
