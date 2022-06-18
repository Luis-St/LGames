package net.vgc.server.game.games.ludo.win;

import net.vgc.game.map.field.GameField;
import net.vgc.server.game.ServerGame;
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
	public boolean hasPlayerFinished(ServerGamePlayer gamePlayer) {
		if (gamePlayer instanceof LudoServerPlayer player) {
			return player.hasAllFiguresAt(GameField::isWin);
		}
		return false;
	}
	
	@Override
	public boolean isDraw(ServerGameMap gameMap) {
		return false;
	}
	
	@Override
	public int getScoreFor(ServerGame game, ServerGamePlayer player) {
		if (!this.winningPlayers.contains(player)) {
			return 0;
		}
		return game.getPlayers().size() - 1 - this.winningPlayers.indexOf(player);
	}
	
}
