package net.vgc.server.game.player;

import net.vgc.game.Game;
import net.vgc.game.player.AbstractGamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.player.Player;

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
	public boolean equals(Object object) {
		if (!super.equals(object)) {
			return false;
		} else if (object instanceof AbstractServerGamePlayer player) {
			return this.rollCount == player.rollCount;
		}
		return false;
	}

}
