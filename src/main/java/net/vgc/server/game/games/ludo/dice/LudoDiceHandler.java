package net.vgc.server.game.games.ludo.dice;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;

import net.vgc.game.map.field.GameField;
import net.vgc.game.player.GamePlayer;
import net.vgc.network.packet.client.game.CanSelectGameFieldPacket;
import net.vgc.server.game.dice.Dice;
import net.vgc.server.game.dice.DiceHandler;
import net.vgc.server.game.dice.PlayerDiceInfo;
import net.vgc.server.game.dice.SimpleDice;
import net.vgc.server.game.games.ludo.LudoServerGame;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Util;

public class LudoDiceHandler implements DiceHandler {
	
	protected final LudoServerGame game;
	protected final int min;
	protected final int max;
	protected final Dice dice;
	protected final List<PlayerDiceInfo> countHistory;
	
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
		return Objects.equals(this.game.getPlayer(), player) && player.getRollCount() > 0;
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
		this.countHistory.add(new PlayerDiceInfo(player, count));
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
	public void performGameAction(GamePlayer gamePlayer, int count) {
		if (gamePlayer.getPlayer() instanceof ServerPlayer player) {
			player.connection.send(new CanSelectGameFieldPacket());
		}
	}

	@Override
	public boolean canRollAfterMove(GamePlayer player, GameField oldField, GameField newField, int count) {
		return Objects.equals(this.game.getPlayer(), player) && count == 6;
	}

	@Override
	public int getLastCount(GamePlayer player) {
		for (PlayerDiceInfo diceInfo : Util.reverseList(this.countHistory)) {
			if (diceInfo.getPlayer().equals(player)) {
				return diceInfo.getCount();
			}
		}
		LOGGER.warn("Player {} has not rolled the dice yet", player.getPlayer().getProfile().getName());
		return -1;
	}

	@Override
	public List<PlayerDiceInfo> getCountHistory() {
		return this.countHistory;
	}
	
	@Override
	public void reset() {
		this.countHistory.clear();
	}
	
}
