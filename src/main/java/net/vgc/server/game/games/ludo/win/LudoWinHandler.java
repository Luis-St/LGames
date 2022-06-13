package net.vgc.server.game.games.ludo.win;

import net.vgc.game.map.field.GameField;
import net.vgc.server.game.games.ludo.player.LudoServerPlayer;
import net.vgc.server.game.map.ServerGameMap;
import net.vgc.server.game.player.ServerGamePlayer;
import net.vgc.server.game.win.AbstractWinHandler;

public class LudoWinHandler extends AbstractWinHandler {

	@Override
	public boolean hasMultipleWinners() {
		return false;
	}
	
	@Override
	public boolean isPlayerFinished(ServerGamePlayer gamePlayer) {
		if (gamePlayer instanceof LudoServerPlayer player) {
			return player.hasAllFiguresAt(GameField::isWin);
		}
		return false;
	}
	
	@Override
	public boolean isDraw(ServerGameMap gameMap) {
		return false;
	}
	
}
