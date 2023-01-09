package net.luis.client.player;

import net.luis.common.player.GameProfile;
import net.luis.game.score.PlayerScore;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class LocalPlayer extends AbstractClientPlayer {
	
	private boolean canSelect = false;
	private boolean canRollDice = false;
	private int count = -1;
	
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LocalPlayer that)) return false;
		if (!super.equals(o)) return false;
		
		if (this.canSelect != that.canSelect) return false;
		if (this.canRollDice != that.canRollDice) return false;
		return this.count == that.count;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.canSelect, this.canRollDice, this.count);
	}
	
}
