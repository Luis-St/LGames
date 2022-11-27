package net.vgc.server.games.ludo.win;

import net.vgc.game.Game;
import net.vgc.game.map.GameMap;
import net.vgc.game.map.field.GameField;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.win.AbstractWinHandler;
import net.vgc.server.games.ludo.player.LudoServerPlayer;

public class LudoWinHandler extends AbstractWinHandler {
	
	@Override
	public boolean hasMultipleWinners() {
		return false;
	}
	
	@Override
	public boolean hasPlayerFinished(GamePlayer gamePlayer) {
		if (gamePlayer instanceof LudoServerPlayer player) {
			return player.hasAllFiguresAt(GameField::isWin);
		}
		return false;
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
