package net.luis.network.packet.client.game;

import net.luis.network.buffer.Encodable;
import net.luis.network.buffer.EncodableObject;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.client.ClientPacket;
import net.luis.network.listener.PacketGetter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-st
 *
 */

public class CurrentPlayerUpdatePacket implements ClientPacket {
	
	private final EncodableObject profile;
	
	public CurrentPlayerUpdatePacket(@NotNull Encodable player) {
		this.profile = new EncodableObject(player);
	}
	
	public CurrentPlayerUpdatePacket(@NotNull FriendlyByteBuffer buffer) {
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
