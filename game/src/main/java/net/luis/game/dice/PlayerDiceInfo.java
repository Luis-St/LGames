package net.luis.game.dice;

import net.luis.game.player.game.GamePlayer;
import net.luis.utils.util.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public record PlayerDiceInfo(@NotNull GamePlayer player, int count) {
	
	@Override
	public boolean equals(@Nullable Object o) {
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
	public @NotNull String toString() {
		return ToString.toString(this);
	}
	
}
