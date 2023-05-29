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

import java.util.*;

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
		this.application = Objects.requireNonNull(application, "Application must not be null");
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
	public void addPlayer(Player player) {
		Objects.requireNonNull(player, "Player must not be null");
		if (this.getPlayer(player.getProfile()) != null) {
			throw new IllegalArgumentException("Player " + player.getName() + " already exists in the player list");
		}
		this.players.put(player.getProfile(), player);
		LOGGER.info("Added player {}", player.getName());
	}
	
	@Override
	public void removePlayer(Player player) {
		Objects.requireNonNull(player, "Player must not be null");
		this.players.remove(player.getProfile());
		LOGGER.info("Removed player {}", player.getName());
	}
	
	@Override
	public Player getPlayer(UUID uniqueId) {
		Objects.requireNonNull(uniqueId, "Unique id must not be null");
		if (Utils.EMPTY_UUID.equals(uniqueId)) {
			return null;
		}
		for (Player player : this.getPlayers()) {
			if (player.getProfile().getUniqueId().equals(uniqueId)) {
				return player;
			}
		}
		return null;
	}
}
