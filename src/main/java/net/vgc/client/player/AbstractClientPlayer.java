package net.vgc.client.player;

import javax.annotation.Nullable;

import com.google.common.base.Objects;

import net.vgc.game.score.PlayerScore;
import net.vgc.newgame.player.GamePlayerType;
import net.vgc.player.GameProfile;
import net.vgc.player.Player;

public abstract class AbstractClientPlayer extends Player {
	
	protected final PlayerScore score;
	protected boolean admin = false;
	protected GamePlayerType type;
	protected boolean current = false;
	protected boolean canSelect;
	
	public AbstractClientPlayer(GameProfile profile, PlayerScore score) {
		super(profile);
		this.score = score;
	}
	
	@Override
	public boolean isClient() {
		return true;
	}
	
	public PlayerScore getScore() {
		return this.score;
	}
	
	public void syncScore(PlayerScore score) {
		this.getScore().setWins(score.getWins());
		this.getScore().setLoses(score.getLoses());
		this.getScore().setDraws(score.getDraws());
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
			} else {
				return this.score.equals(player.score);
			}
		}
		return false;
	}
	
}
