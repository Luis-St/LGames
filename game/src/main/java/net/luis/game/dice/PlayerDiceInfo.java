package net.luis.game.dice;

import net.luis.game.player.game.GamePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public record PlayerDiceInfo(GamePlayer player, int count) {
	
	public PlayerDiceInfo {
		Objects.requireNonNull(player, "Game player must not be null");
	}
	
	//region Object overrides
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
		return "PlayerDiceInfo{player=" + this.player + ", count=" + this.count + "}";
	}
	//endregion
}
