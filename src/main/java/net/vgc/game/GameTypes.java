package net.vgc.game;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.vgc.client.game.ClientGame;
import net.vgc.client.game.games.ludo.LudoClientGame;
import net.vgc.client.game.games.ttt.TTTClientGame;
import net.vgc.client.screen.game.LudoScreen;
import net.vgc.client.screen.game.TTTScreen;
import net.vgc.server.game.ServerGame;
import net.vgc.server.game.games.ludo.LudoServerGame;
import net.vgc.server.game.games.ttt.TTTServerGame;

public class GameTypes {
	
	public static final List<GameType<?, ?>> GAME_TYPES = Lists.newArrayList();
	
	public static final GameType<LudoServerGame, LudoClientGame> LUDO = register(new GameType<>("Ludo", 2, 4, new GameFactory<>(LudoServerGame::new, LudoClientGame::new), LudoScreen::new));
	public static final GameType<TTTServerGame, TTTClientGame> TIC_TAC_TOE = register(new GameType<>("Tic Tac Toe", 2, 2, new GameFactory<>(TTTServerGame::new, TTTClientGame::new), TTTScreen::new));
	
	protected static <S extends ServerGame, C extends ClientGame> GameType<S, C> register(GameType<S, C> gameType) {
		GAME_TYPES.add(gameType);
		return gameType;
	}
	
	@Nullable
	public static GameType<?, ?> fromName(String name) {
		for (GameType<?, ?> gameType : GAME_TYPES) {
			if (gameType.getName().equals(name)) {
				return gameType;
			}
		}
		return null;
	}
	
}
