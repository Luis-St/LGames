package net.vgc.player;

import net.vgc.util.Tickable;

public abstract class Player implements Tickable {
	
	protected final GameProfile profile;
	protected boolean playing;
	
	public Player(GameProfile profile) {
		this.profile = profile;
	}
	
	@Override
	public void tick() {
		
	}
	
	public abstract boolean isClient();
	
	public GameProfile getProfile() {
		return this.profile;
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
		builder.append("profile=").append(this.profile).append("}");
		return builder.toString();
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Player player) {
			if (!this.profile.equals(player.profile)) {
				return false;
			} else {
				return this.playing == player.playing;
			}
		}
		return false;
	}
	
}
