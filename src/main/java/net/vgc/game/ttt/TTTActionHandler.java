package net.vgc.game.ttt;

import net.vgc.game.action.ActionHandler;
import net.vgc.game.action.GameEvent;

public class TTTActionHandler implements ActionHandler {
	
	protected final TTTGame game;
	
	public TTTActionHandler(TTTGame game) {
		this.game = game;
	}
	
	@Override
	public void handle(GameEvent event) {

	}

}
	