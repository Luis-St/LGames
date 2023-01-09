package net.luis.game.player;

import net.luis.game.Game;
import net.luis.player.Player;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractGamePlayer that)) return false;
		
		if (!this.player.equals(that.player)) return false;
		return this.playerType.equals(that.playerType);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.player, this.playerType);
	}
}
