package net.vgc.game.player;

import net.vgc.game.Game;
import net.vgc.player.Player;

public abstract class AbstractGamePlayer implements GamePlayer {
	
	private final Game game;
	private final Player player;
	private final GamePlayerType playerType;
	
	protected AbstractGamePlayer(Game game, Player player, GamePlayerType playerType) {
		this.game = game;
		this.player = player;
		this.playerType = playerType;
	}
	
	@Override
	public Game getGame() {
		return this.game;
	}

	@Override
	public Player getPlayer() {
		return this.player;
	}

	@Override
	public GamePlayerType getPlayerType() {
		return this.playerType;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof AbstractGamePlayer player) {
			if (!this.game.equals(player.game)) {
				return false;
			} else if (!this.player.equals(player.player)) {
				return false;
			} else {
				return this.playerType.equals(player.playerType);
			}
		}
		return false;
	}

}
