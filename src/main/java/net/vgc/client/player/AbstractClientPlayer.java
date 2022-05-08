package net.vgc.client.player;

import net.vgc.player.GameProfile;
import net.vgc.player.Player;

public abstract class AbstractClientPlayer extends Player {
	
	protected boolean admin = false;
	
	public AbstractClientPlayer(GameProfile profile) {
		super(profile);
	}
	
	@Override
	public boolean isClient() {
		return true;
	}
	
	public boolean isAdmin() {
		return this.admin;
	}
	
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
}
