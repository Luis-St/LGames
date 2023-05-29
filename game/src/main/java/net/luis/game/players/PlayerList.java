package net.luis.game.players;

import net.luis.game.application.FxApplication;
import net.luis.game.player.GameProfile;
import net.luis.game.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-st
 *
 */

public interface PlayerList extends Iterable<Player> {
	
	@NotNull FxApplication getApplication();
	
	@NotNull List<Player> getPlayers();
	
	@Override
	default @NotNull Iterator<Player> iterator() {
		return this.getPlayers().iterator();
	}
	
	void addPlayer(Player player);
	
	void removePlayer(Player player);
	
	default void removeAllPlayers() {
		this.getPlayers().forEach(this::removePlayer);
	}
	
	Player getPlayer(UUID uuid);
	
	default Player getPlayer(GameProfile profile) {
		return this.getPlayer(Objects.requireNonNull(profile, "Game profile must not be null").getUniqueId());
	}
	
	default @NotNull List<GameProfile> getProfiles() {
		return this.getPlayers().stream().map(Player::getProfile).collect(Collectors.toList());
	}
}
