package net.vgc.server.game.dice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface Dice {
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	int roll();
	
	int rollExclude(int value);
	
	int rollPreferred(int value, int rolls);
	
}
