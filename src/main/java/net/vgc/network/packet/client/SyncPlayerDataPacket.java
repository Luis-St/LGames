package net.vgc.network.packet.client;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.player.GameProfile;

public class SyncPlayerDataPacket implements ClientPacket {
	
	protected final GameProfile profile;
	protected final boolean playing;
	
	public SyncPlayerDataPacket(GameProfile profile, boolean playing) {
		this.profile = profile;
		this.playing = playing;
	}
	
	public SyncPlayerDataPacket(FriendlyByteBuffer buffer) {
		this.profile = buffer.readProfile();
		this.playing = buffer.readBoolean();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeProfile(this.profile);
		buffer.writeBoolean(this.playing);
	}

	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleSyncPlayerData(this.profile, this.playing);
	}
	
	public GameProfile getProfile() {
		return this.profile;
	}
	
	public boolean isPlaying() {
		return this.playing;
	}

}
