package net.luis.game.dice;

/**
 *
 * @author Luis-st
 *
 */

public interface Dice {
	
	int roll();
	
	int rollExclude(int value);
	
	int rollPreferred(int value, int rolls);
}
