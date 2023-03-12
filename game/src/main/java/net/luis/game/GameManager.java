package net.luis.game;

import com.google.common.collect.Maps;
import net.luis.game.player.GameProfile;
import net.luis.game.player.game.GamePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class GameManager {
	
	private final Map<UUID, Game> games = Maps.newHashMap();
	
	public void addGame(@NotNull Game game) {
		this.games.put(game.getUniqueId(), game);
	}
	
	public @Nullable Game getGame(@NotNull UUID uuid) {
		return this.games.get(uuid);
	}
	
	public @Nullable Game getGameFor(@NotNull GameProfile profile) {
		for (Game game : this.games.values()) {
			if (game.getPlayerFor(profile) != null) {
				return game;
			}
		}
		return null;
	}
	
	public @Nullable Game getGameFor(@NotNull GamePlayer player) {
		return this.getGameFor(player.getPlayer().getProfile());
	}
	
	public void removeGame(@NotNull UUID uuid) {
		this.games.remove(uuid);
	}
	
	public void removeGame(@NotNull Game game) {
		this.removeGame(game.getUniqueId());
	}
	
}
