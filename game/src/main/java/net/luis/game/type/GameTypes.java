package net.luis.game.type;

import com.google.common.collect.Lists;
import net.luis.game.Game;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class GameTypes {
	
	private static final List<GameType<?>> GAME_TYPES = Lists.newArrayList();
	
	// TODO: add Screens -> which can be used outside of client env
	// TODO: add GameFactory
	public static final GameType<?> LUDO = register(new GameType<>(0, "Ludo", 2, 4, null));
	public static final GameType<?> TIC_TAC_TOE = register(new GameType<>(1, "Tic Tac Toe", 2, 2, null));
	public static final GameType<?> WINS_4 = register(new GameType<>(2, "4 Wins", 2, 2, null));
	
	private static <T extends Game> GameType<T> register(GameType<T> gameType) {
		GAME_TYPES.add(gameType);
		return gameType;
	}
	
	@Nullable
	public static GameType<?> fromId(int id) {
		for (GameType<?> gameType : GAME_TYPES) {
			if (gameType.getId() == id) {
				return gameType;
			}
		}
		return null;
	}
	
}
