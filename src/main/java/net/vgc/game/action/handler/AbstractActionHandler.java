package net.vgc.game.action.handler;

import net.vgc.game.Game;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractActionHandler implements ActionHandler {
	
	private final Game game;
	
	public AbstractActionHandler(Game game) {
		this.game = game;
	}
	
	@Override
	public Game getGame() {
		return this.game;
	}
	
}
