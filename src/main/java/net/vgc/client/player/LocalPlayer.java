package net.vgc.client.player;

import net.vgc.game.score.PlayerScore;
import net.vgc.player.GameProfile;

public class LocalPlayer extends AbstractClientPlayer {
	
	protected boolean canSelect = false;
	protected boolean canRollDice = false;
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
	
	public boolean canRollDice() {
		return this.canRollDice;
	}
	
	public void setCanRollDice(boolean canRollDice) {
		this.canRollDice = canRollDice;
	}
	
	public int getCount() {
		return this.count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof LocalPlayer player) {
			if (!super.equals(player)) {
				return false;
			} else if (this.canSelect != player.canSelect) {
				return false;
			} else if (this.canRollDice != player.canRollDice) {
				return false;
			} else {
				return this.count == player.count;
			}
		}
		return false;
	}
	
}
