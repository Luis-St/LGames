package net.vgc.game.action.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.game.Game;
import net.vgc.game.action.GameAction;
import net.vgc.game.map.GameMap;

/**
 *
 * @author Luis-st
 *
 */

public interface GameActionHandler {
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	Game getGame();
	
	default GameMap getMap() {
		return this.getGame().getMap();
	}
	
	boolean handle(GameAction<?> action);
	
}
