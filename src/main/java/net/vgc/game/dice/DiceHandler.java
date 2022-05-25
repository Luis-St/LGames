package net.vgc.game.dice;

import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.game.Game;
import net.vgc.game.player.GamePlayer;
import net.vgc.player.GameProfile;

public interface DiceHandler {
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	Game getGame();
	
	int getMin();
	
	int getMax();
	
	Dice getDice();
	
	boolean canRoll(GamePlayer player);
	
	int roll(GamePlayer player);
	
	boolean canRollAgain(GamePlayer player);
	
	boolean canRollAgainWithResult(GamePlayer player, int count);
	
	int getLastCount(GamePlayer player);
	
	List<Entry<GameProfile, Integer>> getCountHistory();
	
}
