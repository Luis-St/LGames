package net.luis.ludo.dice;

import com.google.common.collect.Lists;
import net.luis.game.Game;
import net.luis.game.dice.Dice;
import net.luis.game.dice.DiceHandler;
import net.luis.game.dice.PlayerDiceInfo;
import net.luis.game.dice.SimpleDice;
import net.luis.game.map.field.GameField;
import net.luis.game.player.game.GamePlayer;
import net.luis.network.packet.client.game.CanSelectGameFieldPacket;
import net.luis.utils.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class LudoDiceHandler implements DiceHandler {
	
	private static final Logger LOGGER = LogManager.getLogger(LudoDiceHandler.class);
	
	private final Game game;
	private final int min;
	private final int max;
	private final Dice dice;
	private final List<PlayerDiceInfo> countHistory = Lists.newArrayList();
	
	public LudoDiceHandler(Game game, int min, int max) {
		this.game = game;
		this.min = min;
		this.max = max;
		this.dice = new SimpleDice(this.min, this.max);
	}
	
	@Override
	public @NotNull Game getGame() {
		return this.game;
	}
	
	@Override
	public int getMin() {
		return this.min;
	}
	
	@Override
	public int getMax() {
		return this.max;
	}
	
	@Override
	public @NotNull Dice getDice() {
		return this.dice;
	}
	
	@Override
	public boolean canRoll(@NotNull GamePlayer player) {
		return Objects.equals(this.game.getPlayer(), player) && player.getRollCount() > 0;
	}
	
	@Override
	public int roll(@NotNull GamePlayer player) {
		return this.handleRolled(player, this.dice.roll());
	}
	
	@Override
	public int rollExclude(@NotNull GamePlayer player, int value) {
		return this.handleRolled(player, this.dice.rollExclude(value));
	}
	
	@Override
	public int rollPreferred(@NotNull GamePlayer player, int value, int rolls) {
		return this.handleRolled(player, this.dice.rollPreferred(value, rolls));
	}
	
	private int handleRolled(GamePlayer player, int count) {
		this.countHistory.add(new PlayerDiceInfo(player, count));
		player.setRollCount(player.getRollCount() - 1);
		return count;
	}
	
	@Override
	public boolean canRollAgain(@NotNull GamePlayer player, int count) {
		return this.canRoll(player) && player.hasAllFiguresAt(GameField::isHome) && count != 6;
	}
	
	@Override
	public boolean canPerformGameAction(@NotNull GamePlayer player, int count) {
		return player.canMoveAnyFigure(count);
	}
	
	@Override
	public void performGameAction(@NotNull GamePlayer gamePlayer, int count) {
		if (!gamePlayer.getPlayer().isClient()) {
			Objects.requireNonNull(gamePlayer.getPlayer().getConnection()).send(new CanSelectGameFieldPacket());
		}
	}
	
	@Override
	public boolean canRollAfterMove(@NotNull GamePlayer player, @NotNull GameField oldField, @NotNull GameField newField, int count) {
		return Objects.equals(this.game.getPlayer(), player) && count == 6;
	}
	
	@Override
	public int getLastCount(@NotNull GamePlayer player) {
		for (PlayerDiceInfo diceInfo : Utils.reverseList(this.countHistory)) {
			if (diceInfo.player().equals(player)) {
				return diceInfo.count();
			}
		}
		LOGGER.warn("Player {} has not rolled the dice yet", player.getPlayer().getProfile().getName());
		return -1;
	}
	
	@Override
	public @NotNull List<PlayerDiceInfo> getCountHistory() {
		return this.countHistory;
	}
	
	@Override
	public void reset() {
		this.countHistory.clear();
	}
	
}