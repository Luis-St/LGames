package net.vgc.player;

import net.vgc.util.Tickable;

public abstract class Player implements Tickable {
	
	protected final GameProfile gameProfile;
	protected boolean playing;
	
	public Player(GameProfile gameProfile) {
		this.gameProfile = gameProfile;
	}
	
	@Override
	public void tick() {
		
	}
	
	public abstract boolean isClient();
	
	public GameProfile getGameProfile() {
		return this.gameProfile;
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
		builder.append("gameProfile=").append(this.gameProfile).append("}");
		return builder.toString();
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Player player) {
			return this.gameProfile.equals(player.gameProfile);
		}
		return false;
	}
	
}
