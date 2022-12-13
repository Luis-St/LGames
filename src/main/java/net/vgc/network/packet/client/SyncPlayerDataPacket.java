package net.vgc.network.packet.client;

import net.vgc.client.network.ClientPacketHandler;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.score.PlayerScore;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.listener.PacketGetter;
import net.vgc.player.GameProfile;
import net.vgc.player.Player;

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
	
	@Override
	public void handle(ClientPacketHandler handler) {
		handler.handleSyncPlayerData(this.profile, this.playing, this.score);
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
