package net.vgc.client.games.ttt;

import net.vgc.client.Client;
import net.vgc.client.game.AbstractClientGame;
import net.vgc.client.games.ttt.map.TTTClientMap;
import net.vgc.client.games.ttt.player.TTTClientPlayer;
import net.vgc.game.player.GamePlayerInfo;
import net.vgc.game.type.GameType;
import net.vgc.game.type.GameTypes;
import net.vgc.server.games.ttt.TTTServerGame;

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
