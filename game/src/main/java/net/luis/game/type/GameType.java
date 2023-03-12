package net.luis.game.type;

import net.luis.game.Game;
import net.luis.game.GameFactory;
import net.luis.game.application.FxApplication;
import net.luis.game.player.game.GamePlayer;
import net.luis.game.player.game.GamePlayerInfo;
import net.luis.utils.math.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class GameType<T extends Game> {
	
	private final int id;
	private final String name;
	private final int minPlayers;
	private final int maxPlayers;
	private final GameFactory<T> gameFactory;
	
	public GameType(int id, @NotNull String name, int minPlayers, int maxPlayers, @NotNull GameFactory<T> gameFactory) {
		this.id = id;
		this.name = name;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.gameFactory = gameFactory;
	}
	
	public int getId() {
		return this.id;
	}
	
	public @NotNull String getName() {
		return this.name;
	}
	
	public @NotNull String getInfoName() {
		return this.name.toLowerCase();
	}
	
	public int getMinPlayers() {
		return this.minPlayers;
	}
	
	public int getMaxPlayers() {
		return this.maxPlayers;
	}
	
	public boolean hasEnoughPlayers(int players) {
		return Mth.isInBounds(players, this.minPlayers, this.maxPlayers);
	}
	
	public boolean hasEnoughPlayers(@NotNull List<GamePlayer> players) {
		return this.hasEnoughPlayers(players.size());
	}
	
	public @NotNull String getBounds() {
		return this.minPlayers + " - " + this.maxPlayers;
	}
	
	public T createGame(@NotNull FxApplication application, @NotNull List<GamePlayerInfo> playerInfos) {
		if (this.hasEnoughPlayers(playerInfos.size())) {
			return this.gameFactory.createGame(application, playerInfos);
		}
		return null;
	}
	
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (!(o instanceof GameType<?> gameType)) return false;
		
		if (this.id != gameType.id) return false;
		if (this.minPlayers != gameType.minPlayers) return false;
		if (this.maxPlayers != gameType.maxPlayers) return false;
		return this.name.equals(gameType.name);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.name, this.minPlayers, this.maxPlayers);
	}
	
	@Override
	public @NotNull String toString() {
		return this.getInfoName();
	}
	
}
