package net.luis.client.games.ludo;

import net.luis.client.Client;
import net.luis.client.game.AbstractClientGame;
import net.luis.client.games.ludo.map.LudoClientMap;
import net.luis.client.games.ludo.player.LudoClientPlayer;
import net.luis.game.player.GamePlayerInfo;
import net.luis.game.type.GameType;
import net.luis.game.type.GameTypes;
import net.luis.server.games.ludo.LudoServerGame;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class LudoClientGame extends AbstractClientGame {
	
	public LudoClientGame(Client client, List<GamePlayerInfo> playerInfos) {
		super(client, LudoClientMap::new, playerInfos, LudoClientPlayer::new);
	}
	
	@Override
	public GameType<LudoServerGame, LudoClientGame> getType() {
		return GameTypes.LUDO;
	}
	
	@Override
	public boolean isDiceGame() {
		return true;
	}
	
	@Override
	public String toString() {
		return "LudoClientGame";
	}
	
}
