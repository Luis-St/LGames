package net.vgc.client.games.wins4;

import java.util.List;

import net.vgc.client.Client;
import net.vgc.client.game.AbstractClientGame;
import net.vgc.client.games.ttt.map.TTTClientMap;
import net.vgc.client.games.ttt.player.TTTClientPlayer;
import net.vgc.game.player.GamePlayerInfo;
import net.vgc.game.type.GameType;
import net.vgc.game.type.GameTypes;
import net.vgc.server.games.wins4.Wins4ServerGame;

public class Wins4ClientGame extends AbstractClientGame {
	
	public Wins4ClientGame(Client client, List<GamePlayerInfo> playerInfos) {
		super(client, TTTClientMap::new, playerInfos, TTTClientPlayer::new);
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
