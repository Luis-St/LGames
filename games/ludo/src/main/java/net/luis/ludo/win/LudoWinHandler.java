package net.luis.ludo.win;

import net.luis.game.Game;
import net.luis.game.map.GameMap;
import net.luis.game.map.field.GameField;
import net.luis.game.player.GamePlayer;
import net.luis.game.win.AbstractWinHandler;

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
	public boolean hasPlayerFinished(GamePlayer gamePlayer) {
		return gamePlayer.hasAllFiguresAt(GameField::isWin);
	}
	
	@Override
	public boolean isDraw(GameMap gameMap) {
		return false;
	}
	
	@Override
	public int getScoreFor(Game game, GamePlayer player) {
		if (!this.winningPlayers.contains(player)) {
			return 0;
		}
		return game.getPlayers().size() - 1 - this.winningPlayers.indexOf(player);
	}
	
}