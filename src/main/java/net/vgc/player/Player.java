package net.vgc.player;

import net.vgc.game.score.PlayerScore;
import net.vgc.util.Tickable;

public abstract class Player implements Tickable {
	
	protected final GameProfile profile;
	protected final PlayerScore score;
	protected boolean playing;
	
	public Player(GameProfile profile, PlayerScore score) {
		this.profile = profile;
		this.score = score;
	}
	
	@Override
	public void tick() {
		
	}
	
	public abstract boolean isClient();
	
	public GameProfile getProfile() {
		return this.profile;
	}
	
	public PlayerScore getScore() {
		return this.score;
	}
	
	public boolean isPlaying() {
		return this.playing;
	}
	
	public void setPlaying(boolean playing) {
		this.playing = playing;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.getClass().getSimpleName() + "{");
		builder.append("profile=").append(this.profile).append(",");
		builder.append("score=").append(this.score).append("}");
		return builder.toString();
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Player player) {
			if (!this.profile.equals(player.profile)) {
				return false;
			} else if (!this.score.equals(player.score)) {
				return false;
			} else {
				return this.playing == player.playing;
			}
		}
		return false;
	}
	
}
