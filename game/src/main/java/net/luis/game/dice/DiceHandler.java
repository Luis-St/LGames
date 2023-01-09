package net.luis.game.dice;

import net.luis.game.Game;
import net.luis.game.map.field.GameField;
import net.luis.game.player.GamePlayer;
import net.luis.utils.math.Mth;
import net.luis.utils.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public interface DiceHandler {
	
	Logger LOGGER = LogManager.getLogger();
	
	Game getGame();
	
	int getMin();
	
	int getMax();
	
	Dice getDice();
	
	boolean canRoll(GamePlayer player);
	
	int roll(GamePlayer player);
	
	int rollExclude(GamePlayer player, int value);
	
	int rollPreferred(GamePlayer player, int value, int rolls);
	
	boolean canRollAgain(GamePlayer player, int count);
	
	boolean canPerformGameAction(GamePlayer player, int count);
	
	void performGameAction(GamePlayer player, int count);
	
	boolean canRollAfterMove(GamePlayer player, GameField oldField, GameField newField, int count);
	
	default boolean hasPlayerRolledDice(GamePlayer player) {
		return Utils.mapList(this.getCountHistory(), PlayerDiceInfo::player).contains(player) && Mth.isInBounds(this.getLastCount(player), this.getMin(), this.getMax());
	}
	
	int getLastCount(GamePlayer player);
	
	List<PlayerDiceInfo> getCountHistory();
	
	void reset();
	
}
