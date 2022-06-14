package net.vgc.server.game.dice;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vgc.game.Game;
import net.vgc.server.game.map.field.ServerGameField;
import net.vgc.server.game.player.ServerGamePlayer;
import net.vgc.util.Mth;
import net.vgc.util.Util;

public interface DiceHandler {
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	Game getGame();
	
	int getMin();
	
	int getMax();
	
	Dice getDice();
	
	boolean canRoll(ServerGamePlayer player);
	
	int roll(ServerGamePlayer player);
	
	int rollExclude(ServerGamePlayer player, int value);
	
	int rollPreferred(ServerGamePlayer player, int value, int rolls);
	
	boolean canRollAgain(ServerGamePlayer player, int count);
	
	boolean canPerformGameAction(ServerGamePlayer player, int count);
	
	void performGameAction(ServerGamePlayer player, int count);
	
	boolean canRollAfterMove(ServerGamePlayer player, ServerGameField field, int count);
	
	default boolean hasPlayerRolledDice(ServerGamePlayer player) {
		return Util.mapList(this.getCountHistory(), PlayerDiceInfo::getPlayer).contains(player) && Mth.isInBounds(this.getLastCount(player), this.getMin(), this.getMax());
	}
	
	int getLastCount(ServerGamePlayer player);
	
	List<PlayerDiceInfo> getCountHistory();
	
}
