package net.luis.network.packet.server.game.dice;

import net.luis.network.buffer.Encodable;
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

public class RollDiceRequestPacket implements ServerPacket {
	
	private final EncodableObject profile;
	
	public RollDiceRequestPacket(@NotNull Encodable profile) {
		this.profile = new EncodableObject(profile);
	}
	
	public RollDiceRequestPacket(@NotNull FriendlyByteBuffer buffer) {
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
