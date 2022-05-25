package net.vgc.game.ludo.dice;

import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import net.vgc.game.dice.Dice;
import net.vgc.game.dice.DiceHandler;
import net.vgc.game.ludo.LudoGame;
import net.vgc.game.ludo.map.LudoMap;
import net.vgc.game.ludo.player.LudoPlayer;
import net.vgc.network.packet.client.game.CanSelectLudoFigurePacket;
import net.vgc.player.GameProfile;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.SimpleEntry;
import net.vgc.util.Util;

public class LudoDiceHandler implements DiceHandler {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected final LudoGame game;
	protected final int min;
	protected final int max;
	protected final Dice dice;
	protected final List<Entry<GameProfile, Integer>> countHistory;
	protected RollCountHolder holder;
	
	public LudoDiceHandler(LudoGame game, int min, int max) {
		this.game = game;
		this.min = min;
		this.max = max;
		this.dice = new Dice(this.min, this.max);
		this.countHistory = Lists.newArrayList();
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
	public int roll(ServerPlayer player) {
		if (this.holder != null) {
			this.holder.decrementRollCount();
		}
		int count = DiceHandler.super.roll(player);
		this.countHistory.add(new SimpleEntry<>(player.getProfile(), count, false));
		return count;
	}
	
	@Override
	public boolean canRoll(ServerPlayer player) {
		return Objects.equals(this.game.getCurrentPlayer(), player) && this.getRollCount(player) > 0;
	}
	
	public boolean canRollAfterMove(ServerPlayer player, int count) {
		return (this.game.getLudoPlayer(player).hasFigureAtStart(this.game.getMap()) && this.game.getLudoPlayer(player).hasFigureAtHome(this.game.getMap())) || count == 6;
	}
	
	@Override
	public boolean canRollAgain(ServerPlayer player, int count) {
		if (this.game.getMap().allFiguresAtHome(this.game.getLudoPlayer(player))) {
			return count != 6 && this.getRollCount(player) > 0;
		}
		return this.canRoll(player) || this.canRollAfterMove(player, count);
	}
	
	@Override
	public boolean handleAfterRoll(ServerPlayer player, int count) {
		if (this.game.getMap().allFiguresAtHome(this.game.getLudoPlayer(player)) && count == 6) {
			this.setRollCount(player, 0);
			player.connection.send(new CanSelectLudoFigurePacket());
			return true;
		} else if (this.game.getMap().canMoveAnyFigure(this.game.getLudoPlayer(player), count)) {
			this.setRollCount(player, 0);
			player.connection.send(new CanSelectLudoFigurePacket());
			return true;
		}
		return false;
	}

	@Override
	public int getRollCount(ServerPlayer player) {
		if (this.holder == null) {
			LudoMap map = this.game.getMap();
			LudoPlayer ludoPlayer = this.game.getLudoPlayer(player);
			if (map.hasFinished(ludoPlayer)) {
				return this.createHolder(player, 0);
			} else if (map.allFiguresAtHome(ludoPlayer)) {
				return this.createHolder(player, 3);
			} else {
				return this.createHolder(player, 1);
			}
		} else {
			if (this.holder.getKey().equals(player.getProfile())) {
				return this.holder.getValue();
			} else if (this.holder.getValue() > 0) {
				LOGGER.warn("Player {} overwrites the roll dice count of player {}", player.getProfile().getName(), this.holder.getKey().getName());
				this.holder = null;
				return this.getRollCount(player);
			} else {
				this.holder = null;
				return this.getRollCount(player);
			}
		}
	}
	
	@Override
	public void setRollCount(ServerPlayer player, int rollCount) {
		if (player == null) {
			if (rollCount == 0) {
				this.holder = null;
			}
		} else if (this.holder != null) {
			if (this.holder.getKey().equals(player.getProfile())) {
				this.holder.setValue(rollCount);
			} else {
				LOGGER.warn("Player {} overwrites the roll dice count of player {}", player.getProfile().getName(), this.holder.getKey().getName());
				this.createHolder(player, rollCount);
			}
		} else {
			this.createHolder(player, rollCount);
		}
	}
	
	protected int createHolder(ServerPlayer player, int rollCount) {
		this.holder = new RollCountHolder(player.getProfile(), rollCount);
		return this.holder.getValue();
	}
	
	@Override
	public int getLastCount(ServerPlayer player) {
		for (Entry<GameProfile, Integer> entry : Util.reverseList(this.countHistory)) {
			if (entry.getKey().equals(player.getProfile())) {
				return entry.getValue();
			}
		}
		LOGGER.warn("Player {} has not rolled the dice yet", player.getProfile().getName());
		return -1;
	}
	
	@Override
	public List<Entry<GameProfile, Integer>> getCountHistory() {
		return this.countHistory;
	}
	
	protected static class RollCountHolder extends SimpleEntry<GameProfile, Integer> {
		
		public RollCountHolder(GameProfile profile, int rollCount) {
			super(profile, rollCount, false);
		}
		
		@Override
		public void setMuted() {
			LOGGER.info("Can not mute the roll count holder");
		}
		
		public void decrementRollCount() {
			this.value = Math.max(0, this.value - 1);
		}
		
	}
	
}
