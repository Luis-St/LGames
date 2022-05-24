package net.vgc.game.dice;

import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.vgc.player.GameProfile;
import net.vgc.server.player.ServerPlayer;

public interface DiceHandler {
	
	int getMin();
	
	int getMax();
	
	Dice getDice();
	
	default int roll(ServerPlayer player) {
		return this.getDice().roll();
	}
	
	boolean canRoll(ServerPlayer player);
	
	boolean canRollAgain(ServerPlayer player, int count);
	
	boolean handleAfterRoll(ServerPlayer player, int count);
	
	int getRollCount(ServerPlayer player);
	
	void setRollCount(@Nullable ServerPlayer player, int rollCount);
	
	default void resetRollCount() {
		this.setRollCount(null, 0);
	}
	
	int getLastCount(ServerPlayer player);
	
	List<Entry<GameProfile, Integer>> getCountHistory();
	
}
