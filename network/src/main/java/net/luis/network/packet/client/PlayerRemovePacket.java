package net.luis.network.packet.client;

import net.luis.network.buffer.Encodable;
import net.luis.network.buffer.EncodableObject;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.listener.PacketGetter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-st
 *
 */

public class PlayerRemovePacket implements ClientPacket {
	
	private final EncodableObject profile;
	
	public PlayerRemovePacket(@NotNull Encodable profile) {
		this.profile = new EncodableObject(profile);
	}
	
	public PlayerRemovePacket(@NotNull FriendlyByteBuffer buffer) {
		this.profile = buffer.read(EncodableObject.class);
	}
	
	@Override
	public void encode(@NotNull FriendlyByteBuffer buffer) {
		buffer.write(this.profile);
	}
	
	@PacketGetter
	public @Nullable Encodable getProfile() {
		return this.profile.get();
	}
	
}
