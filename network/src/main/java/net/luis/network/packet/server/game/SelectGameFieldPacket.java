package net.luis.network.packet.server.game;

import net.luis.network.buffer.Encodable;
import net.luis.network.buffer.EncodableEnum;
import net.luis.network.buffer.EncodableObject;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.listener.PacketGetter;
import net.luis.network.packet.server.ServerPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-st
 *
 */

public class SelectGameFieldPacket implements ServerPacket {
	
	private final EncodableObject profile;
	private final EncodableEnum fieldType;
	private final EncodableObject fieldPos;
	
	public SelectGameFieldPacket(@NotNull Encodable profile, @NotNull Enum<?> fieldType, @NotNull Encodable fieldPos) {
		this.profile = new EncodableObject(profile);
		this.fieldType = new EncodableEnum(fieldType);
		this.fieldPos = new EncodableObject(fieldPos);
	}
	
	public SelectGameFieldPacket(@NotNull FriendlyByteBuffer buffer) {
		this.profile = buffer.read(EncodableObject.class);
		this.fieldType = buffer.read(EncodableEnum.class);
		this.fieldPos = buffer.read(EncodableObject.class);
	}
	
	@Override
	public void encode(@NotNull FriendlyByteBuffer buffer) {
		buffer.write(this.profile);
		buffer.write(this.fieldType);
		buffer.write(this.fieldPos);
	}
	
	@PacketGetter
	public @Nullable Encodable getProfile() {
		return this.profile.get();
	}
	
	@PacketGetter
	public @NotNull Enum<?> getFieldType() {
		return this.fieldType.get();
	}
	
	@PacketGetter
	public @Nullable Encodable getFieldPos() {
		return this.fieldPos.get();
	}
	
}