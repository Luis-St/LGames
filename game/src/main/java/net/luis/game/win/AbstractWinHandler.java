package net.luis.game.win;

import com.google.common.collect.Lists;
import net.luis.game.player.game.GamePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractWinHandler implements WinHandler {
	
	protected final List<GamePlayer> winningPlayers = Lists.newArrayList();
	protected final List<GamePlayer> finishedPlayers = Lists.newArrayList();
	
	@Override
	public boolean hasWinner() {
		return !this.winningPlayers.isEmpty();
	}
	
	@Override
	public @NotNull GamePlayer getWinningPlayer() {
		return this.finishedPlayers.get(0);
	}
	
	@Override
	public boolean hasWinners() {
		return this.hasWinner() && this.winningPlayers.size() > 0;
	}
	
	@Override
	public @NotNull List<GamePlayer> getWinningPlayers() {
		return this.finishedPlayers.subList(0, 1);
	}
	
	@Override
	public void onPlayerFinished(@NotNull GamePlayer player) {
		this.finishedPlayers.add(player);
		if (this.canPlayerWin(player)) {
			this.winningPlayers.add(player);
		}
	}
	
	@Override
	public @NotNull List<GamePlayer> getFinishedPlayers() {
		return this.finishedPlayers;
	}
	
	@Override
	public @NotNull List<GamePlayer> getWinOrder() {
		return this.winningPlayers;
	}
	
	@Override
	public void reset() {
		this.finishedPlayers.clear();
		this.winningPlayers.clear();
	}
}
