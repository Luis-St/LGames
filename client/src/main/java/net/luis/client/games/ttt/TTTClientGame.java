package net.luis.client.games.ttt;

import net.luis.client.Client;
import net.luis.client.game.AbstractClientGame;
import net.luis.client.games.ttt.map.TTTClientMap;
import net.luis.client.games.ttt.player.TTTClientPlayer;
import net.luis.game.player.GamePlayerInfo;
import net.luis.game.type.GameType;
import net.luis.game.type.GameTypes;
import net.luis.server.games.ttt.TTTServerGame;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class TTTClientGame extends AbstractClientGame {
	
	public TTTClientGame(Client client, List<GamePlayerInfo> playerInfos) {
		super(client, TTTClientMap::new, playerInfos, TTTClientPlayer::new);
	}
	
	@Override
	public GameType<TTTServerGame, TTTClientGame> getType() {
		return GameTypes.TIC_TAC_TOE;
	}
	
	@Override
	public String toString() {
		return "TTTClientGame";
	}
	
}
