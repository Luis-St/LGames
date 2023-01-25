package net.luis.client.player;

import net.luis.client.Client;
import net.luis.game.player.GameProfile;
import net.luis.game.player.Player;
import net.luis.game.player.score.PlayerScore;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractClientPlayer extends Player {
	
	private boolean admin = false;
	
	public AbstractClientPlayer(GameProfile profile, PlayerScore score) {
		super(profile, score, Client.getInstance());
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
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractClientPlayer that)) return false;
		if (!super.equals(o)) return false;
		
		return this.admin == that.admin;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.admin);
	}
}
