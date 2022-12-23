package net.vgc.client.games.wins4;

import java.util.List;

import net.vgc.client.Client;
import net.vgc.client.game.AbstractClientGame;
import net.vgc.client.games.wins4.map.Wins4ClientMap;
import net.vgc.client.games.wins4.player.Wins4ClientPlayer;
import net.vgc.game.player.GamePlayerInfo;
import net.vgc.game.type.GameType;
import net.vgc.game.type.GameTypes;
import net.vgc.server.games.wins4.Wins4ServerGame;

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
