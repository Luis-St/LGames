package net.luis.game.player.figure;

import net.luis.game.player.GamePlayer;

import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractGameFigure implements GameFigure {
	
	private final GamePlayer player;
	private final int count;
	private final UUID uuid;
	
	protected AbstractGameFigure(GamePlayer player, int count, UUID uuid) {
		this.player = player;
		this.count = count;
		this.uuid = uuid;
	}
	
	@Override
	public GamePlayer getPlayer() {
		return this.player;
	}
	
	@Override
	public int getCount() {
		return this.count;
	}
	
	@Override
	public UUID getUUID() {
		return this.uuid;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractGameFigure that)) return false;
		
		if (this.count != that.count) return false;
		if (!this.player.equals(that.player)) return false;
		return this.uuid.equals(that.uuid);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.player, this.count, this.uuid);
	}
}
