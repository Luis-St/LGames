package net.vgc.client.player;

import net.vgc.game.score.PlayerScore;
import net.vgc.player.GameProfile;
import net.vgc.player.Player;

public abstract class AbstractClientPlayer extends Player {
	
	private boolean admin = false;
	private boolean current = false;
	
	public AbstractClientPlayer(GameProfile profile, PlayerScore score) {
		super(profile, score);
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
	
	public boolean isCurrent() {
		return this.current;
	}
	
	public void setCurrent(boolean current) {
		this.current = current;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof AbstractClientPlayer player) {
			if (!super.equals(player)) {
				return false;
			} else if (this.admin != player.admin) {
				return false;
			} else {
				return this.current == player.current;
			}
		}
		return false;
	}
	
}
