package net.luis.ludo.win;

import net.luis.game.Game;
import net.luis.game.map.GameMap;
import net.luis.game.map.field.GameField;
import net.luis.game.player.game.GamePlayer;
import net.luis.game.win.AbstractWinHandler;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public class LudoWinHandler extends AbstractWinHandler {
	
	@Override
	public boolean hasMultipleWinners() {
		return false;
	}
	
	@Override
	public boolean hasPlayerFinished(@NotNull GamePlayer gamePlayer) {
		return gamePlayer.hasAllFiguresAt(GameField::isWin);
	}
	
	@Override
	public boolean isDraw(@NotNull GameMap gameMap) {
		return false;
	}
	
	@Override
	public int getScoreFor(@NotNull Game game, @NotNull GamePlayer player) {
		if (!this.winningPlayers.contains(player)) {
			return 0;
		}
		return game.getPlayers().size() - 1 - this.winningPlayers.indexOf(player);
	}
	
}