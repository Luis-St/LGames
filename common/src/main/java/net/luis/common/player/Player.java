package net.luis.common.player;

import net.luis.utils.util.ToString;
import net.luis.game.score.PlayerScore;
import net.luis.common.util.Tickable;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public abstract class Player implements Tickable {
	
	private final GameProfile profile;
	private final PlayerScore score;
	private boolean playing;
	
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
	
	public String getName() {
		return this.getProfile().getName();
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Player player)) return false;
		
		if (this.playing != player.playing) return false;
		if (!this.profile.equals(player.profile)) return false;
		return this.score.equals(player.score);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.profile, this.score, this.playing);
	}
	
	@Override
	public String toString() {
		return ToString.toString(this, "playing");
	}
}
