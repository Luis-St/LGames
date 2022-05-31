package net.vgc.server.game.games.ludo.dice;

import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import com.google.common.collect.Lists;

import net.vgc.game.map.field.GameField;
import net.vgc.game.player.GamePlayer;
import net.vgc.server.game.dice.Dice;
import net.vgc.server.game.dice.DiceHandler;
import net.vgc.server.game.dice.SimpleDice;
import net.vgc.server.game.games.ludo.LudoServerGame;
import net.vgc.util.SimpleEntry;
import net.vgc.util.Util;

public class LudoDiceHandler implements DiceHandler {
	
	protected final LudoServerGame game;
	protected final int min;
	protected final int max;
	protected final Dice dice;
	protected final List<Entry<GamePlayer, Integer>> countHistory;
	
	public LudoDiceHandler(LudoServerGame game, int min, int max) {
		this.game = game;
		this.min = min;
		this.max = max;
		this.dice = new SimpleDice(this.min, this.max);
		this.countHistory = Lists.newArrayList();
	}
	
	@Override
	public LudoServerGame getGame() {
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
	public Dice getDice() {
		return this.dice;
	}

	@Override
	public boolean canRoll(GamePlayer player) {
		return Objects.equals(this.game.getCurrentPlayer(), player) && player.getRollCount() > 0;
	}

	@Override
	public int roll(GamePlayer player) {
		return this.handleRolled(player, this.dice.roll());
	}

	@Override
	public int rollExclude(GamePlayer player, int value) {
		return this.handleRolled(player, this.dice.rollExclude(value));
	}

	@Override
	public int rollPreferred(GamePlayer player, int value, int rolls) {
		return this.handleRolled(player, this.dice.rollPreferred(value, rolls));
	}
	
	protected int handleRolled(GamePlayer player, int count) {
		this.countHistory.add(new SimpleEntry<>(player, count, true));
		player.setRollCount(player.getRollCount() - 1);
		return count;
	}

	@Override
	public boolean canRollAgain(GamePlayer player, int count) {
		return this.canRoll(player) && player.hasAllFiguresAt(GameField::isHome) && count != 6;
	}
	
	@Override
	public boolean canPerformGameAction(GamePlayer player, int count) {
		return player.canMoveAnyFigure(count);
	}

	@Override
	public boolean canRollAfterMove(GamePlayer player, GameField field, int count) {
		return Objects.equals(this.game.getCurrentPlayer(), player) && count == 6;
	}

	@Override
	public int getLastCount(GamePlayer player) {
		for (Entry<GamePlayer, Integer> entry : Util.reverseList(this.countHistory)) {
			if (entry.getKey().equals(player)) {
				return entry.getValue();
			}
		}
		LOGGER.warn("Player {} has not rolled the dice yet", player.getPlayer().getProfile().getName());
		return -1;
	}

	@Override
	public List<Entry<GamePlayer, Integer>> getCountHistory() {
		return this.countHistory;
	}

}
