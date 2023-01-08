package net.vgc.client.player;

import net.vgc.game.score.PlayerScore;
import net.vgc.player.GameProfile;
import net.vgc.player.Player;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractClientPlayer that)) return false;
		if (!super.equals(o)) return false;
		
		if (this.admin != that.admin) return false;
		return this.current == that.current;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.admin, this.current);
	}
	
}
