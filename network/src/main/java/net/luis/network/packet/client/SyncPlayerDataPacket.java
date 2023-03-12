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

public class SyncPlayerDataPacket implements ClientPacket {
	
	private final EncodableObject profile;
	private final boolean playing;
	private final EncodableObject score;
	
	public SyncPlayerDataPacket(@NotNull Encodable profile, boolean playing, @NotNull Encodable score) {
		this.profile = new EncodableObject(profile);
		this.playing = playing;
		this.score = new EncodableObject(score);
	}
	
	public SyncPlayerDataPacket(@NotNull FriendlyByteBuffer buffer) {
		this.profile = buffer.read(EncodableObject.class);
		this.playing = buffer.readBoolean();
		this.score = buffer.read(EncodableObject.class);
	}
	
	@Override
	public void encode(@NotNull FriendlyByteBuffer buffer) {
		buffer.write(this.profile);
		buffer.writeBoolean(this.playing);
		buffer.write(this.score);
	}
	
	@PacketGetter
	public @Nullable Encodable getProfile() {
		return this.profile.get();
	}
	
	@PacketGetter
	public boolean isPlaying() {
		return this.playing;
	}
	
	@PacketGetter
	public @Nullable Encodable getScore() {
		return this.score.get();
	}
	
}
