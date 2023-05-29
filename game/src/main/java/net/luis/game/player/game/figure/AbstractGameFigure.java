package net.luis.game.player.game.figure;

import net.luis.game.player.game.GamePlayer;
import org.jetbrains.annotations.NotNull;

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
	
	protected AbstractGameFigure(GamePlayer player, int index, UUID uniqueId) {
		this.player = Objects.requireNonNull(player, "Game player must not be null");
		this.index = index;
		this.uniqueId = Objects.requireNonNull(uniqueId, "Unique id must not be null");
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
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
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
	//endregion
}
