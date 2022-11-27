package net.vgc.game.win;

import java.util.List;

import com.google.common.collect.Lists;

import net.vgc.game.player.GamePlayer;

public abstract class AbstractWinHandler implements WinHandler {
	
	protected final List<GamePlayer> winningPlayers = Lists.newArrayList();
	protected final List<GamePlayer> finishedPlayers = Lists.newArrayList();
	
	@Override
	public boolean hasWinner() {
		return !this.winningPlayers.isEmpty();
	}
	
	@Override
	public GamePlayer getWinningPlayer() {
		return this.finishedPlayers.get(0);
	}
	
	@Override
	public boolean hasWinners() {
		return this.hasWinner() && this.winningPlayers.size() > 0;
	}
	
	@Override
	public List<GamePlayer> getWinningPlayers() {
		return this.finishedPlayers.subList(0, 1);
	}
	
	@Override
	public void onPlayerFinished(GamePlayer player) {
		this.finishedPlayers.add(player);
		if (this.canPlayerWin(player)) {
			this.winningPlayers.add(player);
		}
	}
	
	@Override
	public List<GamePlayer> getFinishedPlayers() {
		return this.finishedPlayers;
	}
	
	@Override
	public List<GamePlayer> getWinOrder() {
		return this.winningPlayers;
	}
	
	@Override
	public void reset() {
		this.finishedPlayers.clear();
		this.winningPlayers.clear();
	}
	
}
