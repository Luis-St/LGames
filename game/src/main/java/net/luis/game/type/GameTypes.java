package net.luis.game.type;

import com.google.common.collect.Lists;
import net.luis.game.Game;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class GameTypes {
	
	private static final List<GameType<?>> GAME_TYPES = Lists.newArrayList();
	
	public static final GameType<?> LUDO = register(new GameType<>(0, "Ludo", 2, 4, 4, "net.luis.ludo.LudoGame"));
	public static final GameType<?> TIC_TAC_TOE = register(new GameType<>(1, "Tic Tac Toe", 2, 2, 5, "net.luis.ttt.TTTGame"));
	public static final GameType<?> WINS_4 = register(new GameType<>(2, "4 Wins", 2, 2, 22, "net.luis.wins4.Wins4Game"));
	
	private static <T extends Game> @NotNull GameType<T> register(GameType<T> gameType) {
		Objects.requireNonNull(gameType, "Game type must not be null");
		GAME_TYPES.add(gameType);
		return gameType;
	}
	
	public static GameType<?> fromId(int id) {
		for (GameType<?> gameType : GAME_TYPES) {
			if (gameType.getId() == id) {
				return gameType;
			}
		}
		return null;
	}
}
