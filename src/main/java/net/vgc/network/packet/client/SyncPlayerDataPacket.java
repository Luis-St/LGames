package net.vgc.network.packet.client;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.score.PlayerScore;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.player.GameProfile;
import net.vgc.player.Player;

public class SyncPlayerDataPacket implements ClientPacket {
	
	protected final GameProfile profile;
	protected final boolean playing;
	protected final PlayerScore score;
	
	public SyncPlayerDataPacket(GamePlayer player) {
		this(player.getPlayer());
	}
	
	public SyncPlayerDataPacket(Player player) {
		this(player.getProfile(), player.isPlaying(), player.getScore());
	}
	
	public SyncPlayerDataPacket(GameProfile profile, boolean playing, PlayerScore score) {
		this.profile = profile;
		this.playing = playing;
		this.score = score;
	}
	
	public SyncPlayerDataPacket(FriendlyByteBuffer buffer) {
		this.profile = buffer.read(GameProfile.class);
		this.playing = buffer.readBoolean();
		this.score = buffer.read(PlayerScore.class);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.profile);
		buffer.writeBoolean(this.playing);
		buffer.write(this.score);
	}

	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleSyncPlayerData(this.profile, this.playing, this.score);
	}
	
	public GameProfile getProfile() {
		return this.profile;
	}
	
	public boolean isPlaying() {
		return this.playing;
	}
	
	public PlayerScore getScore() {
		return this.score;
	}

}
