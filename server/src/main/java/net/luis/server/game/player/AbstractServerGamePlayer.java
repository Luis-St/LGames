package net.luis.server.game.player;

import net.luis.game.Game;
import net.luis.game.player.AbstractGamePlayer;
import net.luis.game.player.GamePlayerType;
import net.luis.common.player.Player;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractServerGamePlayer extends AbstractGamePlayer {
	
	private int rollCount = 0;
	
	protected AbstractServerGamePlayer(Game game, Player player, GamePlayerType playerType) {
		super(game, player, playerType);
	}
	
	@Override
	public int getRollCount() {
		return this.getGame().isDiceGame() ? this.rollCount : 0;
	}
	
	@Override
	public void setRollCount(int rollCount) {
		this.rollCount = Math.max(0, rollCount);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractServerGamePlayer that)) return false;
		if (!super.equals(o)) return false;
		
		return this.rollCount == that.rollCount;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.rollCount);
	}
}
