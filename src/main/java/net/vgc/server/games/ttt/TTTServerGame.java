package net.vgc.server.games.ttt;

import java.util.List;

import net.vgc.client.games.ttt.TTTClientGame;
import net.vgc.game.type.GameType;
import net.vgc.game.type.GameTypes;
import net.vgc.games.ttt.player.TTTPlayerType;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.game.AbstractServerGame;
import net.vgc.server.games.ttt.action.TTTServerActionHandler;
import net.vgc.server.games.ttt.map.TTTServerMap;
import net.vgc.server.games.ttt.player.TTTServerPlayer;
import net.vgc.server.games.ttt.win.TTTWinHandler;
import net.vgc.server.player.ServerPlayer;

public class TTTServerGame extends AbstractServerGame {
	
	public TTTServerGame(DedicatedServer server, List<ServerPlayer> players) {
		super(server, TTTServerMap::new, players, TTTPlayerType.values(), TTTServerPlayer::new, new TTTWinHandler(), TTTServerActionHandler::new);
	}
	
	@Override
	public GameType<TTTServerGame, TTTClientGame> getType() {
		return GameTypes.TIC_TAC_TOE;
	}
	
	@Override
	public String toString() {
		return "TTTServerGame";
	}
	
}
