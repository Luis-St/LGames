package net.vgc.server.game.win;

import java.util.List;

import com.google.common.collect.Lists;

import net.vgc.server.game.player.ServerGamePlayer;

public abstract class AbstractWinHandler implements WinHandler {
	
	protected final List<ServerGamePlayer> winningPlayers = Lists.newArrayList();
	protected final List<ServerGamePlayer> finishedPlayers = Lists.newArrayList();

	@Override
	public boolean hasWinner() {
		return !this.winningPlayers.isEmpty();
	}

	@Override
	public ServerGamePlayer getWinningPlayer() {
		return this.finishedPlayers.get(0);
	}

	@Override
	public boolean hasWinners() {
		return this.hasWinner() && this.winningPlayers.size() > 0;
	}

	@Override
	public List<? extends ServerGamePlayer> getWinningPlayers() {
		return this.finishedPlayers.subList(0, 1);
	}

	@Override
	public void onPlayerFinished(ServerGamePlayer player) {
		this.finishedPlayers.add(player);
		if (this.canPlayerWin(player)) {
			this.winningPlayers.add(player);
		}
	}

	@Override
	public List<? extends ServerGamePlayer> getFinishedPlayers() {
		return this.finishedPlayers;
	}

	@Override
	public List<? extends ServerGamePlayer> getWinOrder() {
		return this.winningPlayers;
	}
	
	@Override
	public void reset() {
		this.finishedPlayers.clear();
		this.winningPlayers.clear();
	}
	
}
