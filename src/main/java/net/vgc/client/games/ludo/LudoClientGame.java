package net.vgc.client.games.ludo;

import java.util.List;

import net.vgc.client.Client;
import net.vgc.client.game.AbstractClientGame;
import net.vgc.client.games.ludo.map.LudoClientMap;
import net.vgc.client.games.ludo.player.LudoClientPlayer;
import net.vgc.game.player.GamePlayerInfo;
import net.vgc.game.type.GameType;
import net.vgc.game.type.GameTypes;
import net.vgc.server.games.ludo.LudoServerGame;

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
