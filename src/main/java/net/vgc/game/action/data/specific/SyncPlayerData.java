package net.vgc.game.action.data.specific;

import net.vgc.game.action.data.GameActionData;
import net.vgc.game.score.PlayerScore;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.player.GameProfile;

/**
 * 
 * @author Luis Staudt
 * 
 */

public class SyncPlayerData extends GameActionData {
	
	private final GameProfile profile;
	private final boolean playing;
	private final PlayerScore score;
	
	public SyncPlayerData(GameProfile profile, boolean playing, PlayerScore score) {
		super();
		this.profile = profile;
		this.playing = playing;
		this.score = score;
	}
	
	public SyncPlayerData(FriendlyByteBuffer buffer) {
		super(buffer);
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
