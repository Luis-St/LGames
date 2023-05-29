package net.luis.game;

import com.google.common.collect.Maps;
import net.luis.game.player.GameProfile;
import net.luis.game.player.game.GamePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 *
 * @author Luis-st
 *
 */

public class GameManager {
	
	private final Map<UUID, Game> games = Maps.newHashMap();
	
	public void addGame(Game game) {
		Objects.requireNonNull(game, "Game must not be null");
		this.games.put(game.getUniqueId(), game);
	}
	
	public Game getGame(UUID uniqueId) {
		return this.games.get(uniqueId);
	}
	
	public Game getGameFor(GameProfile profile) {
		return this.games.values().stream().filter(game -> game.getPlayerFor(profile) != null).findFirst().orElse(null);
	}
	
	public Game getGameFor(GamePlayer player) {
		return this.getGameFor(Objects.requireNonNull(player, "Player must not be null").getProfile());
	}
	
	public void removeGame(UUID uniqueId) {
		this.games.remove(uniqueId);
	}
	
	public void removeGame(Game game) {
		this.removeGame(Objects.requireNonNull(game, "Game must not be null").getUniqueId());
	}
}
