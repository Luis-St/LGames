package net.luis.game.player.game.figure;

import net.luis.game.player.game.GamePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractGameFigure implements GameFigure {
	
	private final GamePlayer player;
	private final int index;
	private final UUID uniqueId;
	
	protected AbstractGameFigure(@NotNull GamePlayer player, int index, @NotNull UUID uniqueId) {
		this.player = player;
		this.index = index;
		this.uniqueId = uniqueId;
	}
	
	@Override
	public @NotNull GamePlayer getPlayer() {
		return this.player;
	}
	
	@Override
	public int getIndex() {
		return this.index;
	}
	
	@Override
	public @NotNull UUID getUniqueId() {
		return this.uniqueId;
	}
	
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractGameFigure that)) return false;
		
		if (this.index != that.index) return false;
		if (!this.player.equals(that.player)) return false;
		return this.uniqueId.equals(that.uniqueId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.player, this.index, this.uniqueId);
	}
}