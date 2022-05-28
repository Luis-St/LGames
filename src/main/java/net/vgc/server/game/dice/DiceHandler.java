package net.vgc.server.game.dice;

import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.game.Game;
import net.vgc.game.map.field.GameField;
import net.vgc.game.player.GamePlayer;

public interface DiceHandler {
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	Game getGame();
	
	int getMin();
	
	int getMax();
	
	Dice getDice();
	
	boolean canRoll(GamePlayer player);
	
	int roll(GamePlayer player);
	
	int rollExclude(GamePlayer player, int value);
	
	int rollPreferred(GamePlayer player, int value, int rolls);
	
	boolean canRollAgain(GamePlayer player, int count);
	
	boolean canRollAfterMove(GamePlayer player, GameField field, int count);
	
	int getLastCount(GamePlayer player);
	
	List<Entry<GamePlayer, Integer>> getCountHistory();
	
}
