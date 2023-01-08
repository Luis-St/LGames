package net.vgc.game.dice;

import net.luis.utils.util.ToString;
import net.vgc.game.player.GamePlayer;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public record PlayerDiceInfo(GamePlayer player, int count) {
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PlayerDiceInfo that)) return false;
		
		if (this.count != that.count) return false;
		return this.player.equals(that.player);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.player, this.count);
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
}
