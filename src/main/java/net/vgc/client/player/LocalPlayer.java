package net.vgc.client.player;

import net.vgc.game.score.PlayerScore;
import net.vgc.player.GameProfile;

public class LocalPlayer extends AbstractClientPlayer {
	
	protected int count = -1;
	
	public LocalPlayer(GameProfile profile) {
		super(profile, new PlayerScore(profile));
	}
	
	public boolean canSelect() {
		return this.canSelect;
	}
	
	public void setCanSelect(boolean canSelect) {
		this.canSelect = canSelect;
	}
	
	public int getCount() {
		return this.count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}

}
