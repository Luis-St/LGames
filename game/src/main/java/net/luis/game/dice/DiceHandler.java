package net.luis.game.dice;

import net.luis.game.Game;
import net.luis.game.map.field.GameField;
import net.luis.game.player.game.GamePlayer;
import net.luis.utils.math.Mth;
import net.luis.utils.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public interface DiceHandler {
	
	@NotNull Game getGame();
	
	int getMin();
	
	int getMax();
	
	@NotNull Dice getDice();
	
	boolean canRoll(@NotNull GamePlayer player);
	
	int roll(@NotNull GamePlayer player);
	
	int rollExclude(@NotNull GamePlayer player, int value);
	
	int rollPreferred(@NotNull GamePlayer player, int value, int rolls);
	
	boolean canRollAgain(@NotNull GamePlayer player, int count);
	
	boolean canPerformGameAction(@NotNull GamePlayer player, int count);
	
	void performGameAction(@NotNull GamePlayer player, int count);
	
	boolean canRollAfterMove(@NotNull GamePlayer player, @NotNull GameField oldField, @NotNull GameField newField, int count);
	
	default boolean hasPlayerRolledDice(@NotNull GamePlayer player) {
		return Utils.mapList(this.getCountHistory(), PlayerDiceInfo::player).contains(player) && Mth.isInBounds(this.getLastCount(player), this.getMin(), this.getMax());
	}
	
	int getLastCount(@NotNull GamePlayer player);
	
	@NotNull List<PlayerDiceInfo> getCountHistory();
	
	void reset();
}
