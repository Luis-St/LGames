package net.luis.game.type;

import net.luis.game.Game;
import net.luis.game.player.game.GamePlayerInfo;
import net.luis.utils.math.Mth;
import net.luis.utils.util.unsafe.reflection.ReflectionHelper;
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
	private final int figureCount;
	private final String clazz;
	
	public GameType(int id, String name, int minPlayers, int maxPlayers, int figureCount, String clazz) {
		this.id = id;
		this.name = Objects.requireNonNull(name, "Name must not be null");
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.figureCount = figureCount;
		this.clazz = Objects.requireNonNull(clazz, "Class must not be null");
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
	
	public @NotNull String getBounds() {
		return this.minPlayers + " - " + this.maxPlayers;
	}
	
	public int getFigureCount() {
		return this.figureCount;
	}
	
	@SuppressWarnings("unchecked")
	public T createGame(List<GamePlayerInfo> playerInfos) {
		Objects.requireNonNull(playerInfos, "Player infos must not be null");
		if (this.hasEnoughPlayers(playerInfos.size())) {
			return (T) ReflectionHelper.newInstance(Objects.requireNonNull(ReflectionHelper.getClassForName(this.clazz)), playerInfos);
		}
		return null;
	}
	
	//region Object overrides
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (!(o instanceof GameType<?> gameType)) return false;
		
		if (this.id != gameType.id) return false;
		if (this.minPlayers != gameType.minPlayers) return false;
		if (this.maxPlayers != gameType.maxPlayers) return false;
		if (this.figureCount != gameType.figureCount) return false;
		return this.name.equals(gameType.name);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.name, this.minPlayers, this.maxPlayers, this.figureCount);
	}
	
	@Override
	public @NotNull String toString() {
		return this.getInfoName();
	}
	//endregion
}
