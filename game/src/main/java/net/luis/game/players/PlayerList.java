package net.luis.game.players;

import net.luis.game.application.FxApplication;
import net.luis.game.player.GameProfile;
import net.luis.game.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
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
	
	void addPlayer(@NotNull Player player);
	
	void removePlayer(@NotNull Player player);
	
	default void removeAllPlayers() {
		this.getPlayers().forEach(this::removePlayer);
	}
	
	@Nullable Player getPlayer(@NotNull UUID uuid);
	
	default @Nullable Player getPlayer(@NotNull GameProfile profile) {
		return this.getPlayer(profile.getUniqueId());
	}
	
	@NotNull
	default List<GameProfile> getProfiles() {
		return this.getPlayers().stream().map(Player::getProfile).collect(Collectors.toList());
	}
	
}
