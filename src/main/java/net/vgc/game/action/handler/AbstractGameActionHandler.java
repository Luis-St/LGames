package net.vgc.game.action.handler;

import net.vgc.game.Game;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractGameActionHandler implements GameActionHandler {
	
	private final Game game;
	
	public AbstractGameActionHandler(Game game) {
		this.game = game;
	}
	
	@Override
	public Game getGame() {
		return this.game;
	}
	
}
