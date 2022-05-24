package net.vgc.client.player;

import javax.annotation.Nullable;

import com.google.common.base.Objects;

import net.vgc.game.GamePlayerType;
import net.vgc.player.GameProfile;
import net.vgc.player.Player;

public abstract class AbstractClientPlayer extends Player {
	
	protected boolean admin = false;
	protected GamePlayerType type;
	protected boolean current = false;
	protected boolean canSelect;
	protected int wins = 0;
	protected int loses = 0;
	protected int draws = 0;
	
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
	
	@Nullable
	public GamePlayerType getType() {
		return this.type;
	}
	
	public void setType(GamePlayerType type) {
		this.type = type;
	}
	
	public boolean isCurrent() {
		return this.current;
	}
	
	public void setCurrent(boolean current) {
		this.current = current;
	}
	
	public boolean canSelect() {
		return this.canSelect;
	}
	
	public void setCanSelect(boolean canSelect) {
		this.canSelect = canSelect;
	}
	
	public int getWins() {
		return this.wins;
	}
	
	public void setWins(int wins) {
		this.wins = wins;
	}
	
	public int getLoses() {
		return this.loses;
	}
	
	public void setLoses(int loses) {
		this.loses = loses;
	}
	
	public int getDraws() {
		return this.draws;
	}
	
	public void setDraws(int draws) {
		this.draws = draws;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof AbstractClientPlayer player) {
			if (!super.equals(player)) {
				return false;
			} else if (this.admin != player.admin) {
				return false;
			} else if (!Objects.equal(this.type, player.type)) {
				return false;
			} else if (this.current != player.current) {
				return false;
			} else if (this.wins != player.wins) {
				return false;
			} else if (this.loses != player.loses) {
				return false;
			} else {
				return this.draws == player.loses;
			}
		}
		return false;
	}
	
}
