package net.vgc.game.player.figure;

import java.util.UUID;

import net.vgc.game.player.GamePlayer;

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
	public boolean equals(Object object) {
		if (object instanceof AbstractGameFigure figure) {
			if (!this.player.equals(figure.player)) {
				return false;
			} else if (this.count != figure.count) {
				return false;
			} else {
				return this.uuid.equals(figure.uuid);
			}
		}
		return false;
	}
	
}
