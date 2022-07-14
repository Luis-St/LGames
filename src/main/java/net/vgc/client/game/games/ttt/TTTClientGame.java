package net.vgc.client.game.games.ttt;

import java.util.List;

import net.vgc.client.Client;
import net.vgc.client.game.AbstractClientGame;
import net.vgc.client.game.games.ttt.map.TTTClientMap;
import net.vgc.client.game.games.ttt.player.TTTClientPlayer;
import net.vgc.game.player.GamePlayerInfo;
import net.vgc.game.type.GameType;
import net.vgc.game.type.GameTypes;
import net.vgc.server.game.games.ttt.TTTServerGame;

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
