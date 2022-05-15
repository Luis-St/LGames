package net.vgc.game;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.vgc.client.screen.game.TTTScreen;
import net.vgc.game.ttt.TTTGame;
import net.vgc.network.packet.client.game.StartTTTGamePacket;

public class GameTypes {
	
	public static final List<GameType<?>> GAME_TYPES = Lists.newArrayList();
	
	public static final GameType<TTTGame> TIC_TAC_TOE = register(new GameType<>("Tic Tac Toe", 2, 2, TTTGame::new, StartTTTGamePacket::new, TTTScreen::new));
	
	protected static <T extends Game> GameType<T> register(GameType<T> gameType) {
		GAME_TYPES.add(gameType);
		return gameType;
	}
	
	@Nullable
	public static GameType<?> fromName(String name) {
		for (GameType<?> gameType : GAME_TYPES) {
			if (gameType.getName().equals(name)) {
				return gameType;
			}
		}
		return null;
	}
	
}
