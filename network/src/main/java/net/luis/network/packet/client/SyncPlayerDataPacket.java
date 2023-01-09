package net.luis.network.packet.client;

import net.luis.common.player.GameProfile;
import net.luis.common.player.Player;
import net.luis.game.player.GamePlayer;
import net.luis.game.score.PlayerScore;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.listener.PacketGetter;

/**
 *
 * @author Luis-st
 *
 */

public class SyncPlayerDataPacket implements ClientPacket {
	
	private final GameProfile profile;
	private final boolean playing;
	private final PlayerScore score;
	
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
	
	@PacketGetter
	public GameProfile getProfile() {
		return this.profile;
	}
	
	@PacketGetter
	public boolean isPlaying() {
		return this.playing;
	}
	
	@PacketGetter
	public PlayerScore getScore() {
		return this.score;
	}
	
}
