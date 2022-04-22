package net.vgc.player;

import net.vgc.util.Tickable;

public abstract class Player implements Tickable {
	
	protected final GameProfile gameProfile;
	
	public Player(GameProfile gameProfile) {
		this.gameProfile = gameProfile;
	}
	
	public abstract boolean isClient();
	
	public GameProfile getGameProfile() {
		return this.gameProfile;
	}
	
	@Override
	public void tick() {
		
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
