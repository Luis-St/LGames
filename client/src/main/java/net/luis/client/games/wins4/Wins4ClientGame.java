package net.luis.client.games.wins4;

import net.luis.client.Client;
import net.luis.client.game.AbstractClientGame;
import net.luis.client.games.wins4.map.Wins4ClientMap;
import net.luis.client.games.wins4.player.Wins4ClientPlayer;
import net.luis.game.player.GamePlayerInfo;
import net.luis.game.type.GameType;
import net.luis.game.type.GameTypes;
import net.luis.server.games.wins4.Wins4ServerGame;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class Wins4ClientGame extends AbstractClientGame {
	
	public Wins4ClientGame(Client client, List<GamePlayerInfo> playerInfos) {
		super(client, Wins4ClientMap::new, playerInfos, Wins4ClientPlayer::new);
	}
	
	@Override
	public GameType<Wins4ServerGame, Wins4ClientGame> getType() {
		return GameTypes.WINS_4;
	}
	
	@Override
	public String toString() {
		return "Win4ClientGame";
	}
	
}
