package net.vgc.player;

import net.vgc.game.score.PlayerScore;
import net.vgc.util.Tickable;

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
	public String toString() {
		String builder = this.getClass().getSimpleName() + "{" + "profile=" + this.profile + "," +
				"score=" + this.score + "}";
		return builder;
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
