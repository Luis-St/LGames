package net.luis.game.dice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Luis-st
 *
 */

public interface Dice {
	
	Logger LOGGER = LogManager.getLogger();
	
	int roll();
	
	int rollExclude(int value);
	
	int rollPreferred(int value, int rolls);
	
}
