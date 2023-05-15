package net.luis.game.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.luis.game.application.FxApplication;
import net.luis.game.player.GameProfile;
import net.luis.game.player.Player;
import net.luis.utils.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractPlayerList implements PlayerList {
	
	private static final Logger LOGGER = LogManager.getLogger(AbstractPlayerList.class);
	protected final Map<GameProfile, Player> players = Maps.newHashMap();
	private final FxApplication application;
	
	public AbstractPlayerList(FxApplication application) {
		this.application = application;
	}
	
	@Override
	public @NotNull FxApplication getApplication() {
		return this.application;
	}
	
	@Override
	public @NotNull List<Player> getPlayers() {
		return Lists.newArrayList(this.players.values());
	}
	
	@Override
	public void addPlayer(@NotNull Player player) {
		if (this.getPlayer(player.getProfile()) != null) {
			throw new IllegalArgumentException("Player " + player.getName() + " already exists in the player list");
		}
		this.players.put(player.getProfile(), player);
		LOGGER.info("Added player {}", player.getName());
	}
	
	@Override
	public void removePlayer(@NotNull Player player) {
		this.players.remove(player.getProfile());
		LOGGER.info("Removed player {}", player.getName());
	}
	
	@Override
	public Player getPlayer(@NotNull UUID uuid) {
		if (Utils.EMPTY_UUID.equals(uuid)) {
			return null;
		}
		for (Player player : this.getPlayers()) {
			if (player.getProfile().getUniqueId().equals(uuid)) {
				return player;
			}
		}
		return null;
	}
}
