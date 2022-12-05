package net.vgc.server.games.wins4;

import java.util.List;

import net.vgc.client.games.wins4.Wins4ClientGame;
import net.vgc.game.type.GameType;
import net.vgc.game.type.GameTypes;
import net.vgc.games.wins4.player.Wins4PlayerType;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.game.AbstractServerGame;
import net.vgc.server.games.ttt.player.TTTServerPlayer;
import net.vgc.server.games.wins4.action.Wins4ServerActionHandler;
import net.vgc.server.games.wins4.map.Wins4ServerMap;
import net.vgc.server.games.wins4.win.Wins4WinHandler;
import net.vgc.server.player.ServerPlayer;

/**
 *
 * @author Luis-st
 *
 */

public class Wins4ServerGame extends AbstractServerGame {
	
	public Wins4ServerGame(DedicatedServer server, List<ServerPlayer> players) {
		super(server, Wins4ServerMap::new, players, Wins4PlayerType.values(), TTTServerPlayer::new, new Wins4WinHandler(), Wins4ServerActionHandler::new);
	}
	
	@Override
	public GameType<Wins4ServerGame, Wins4ClientGame> getType() {
		return GameTypes.WINS_4;
	}
	
	@Override
	public String toString() {
		return "Win4ServerGame";
	}
	
}
