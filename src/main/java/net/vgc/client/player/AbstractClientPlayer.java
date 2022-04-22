package net.vgc.client.player;

import net.vgc.player.GameProfile;
import net.vgc.player.Player;

public abstract class AbstractClientPlayer extends Player {

	public AbstractClientPlayer(GameProfile gameProfile) {
		super(gameProfile);
	}
	
	@Override
	public boolean isClient() {
		return true;
	}
	
}
