package net.vgc.client.game.player;

import net.vgc.game.Game;
import net.vgc.game.player.AbstractGamePlayer;
import net.vgc.game.player.GamePlayerType;
import net.vgc.player.Player;

public abstract class AbstractClientGamePlayer extends AbstractGamePlayer {

	protected AbstractClientGamePlayer(Game game, Player player, GamePlayerType playerType) {
		super(game, player, playerType);
	}

	@Override
	public int getRollCount() {
		LOGGER.warn("Can not get the roll count of player {}, on client", this.getPlayer().getProfile().getName());
		return 0;
	}

	@Override
	public void setRollCount(int rollCount) {
		LOGGER.warn("Can not set the roll count of player {} to {}, on client", this.getPlayer().getProfile().getName(), rollCount);
	}

}
