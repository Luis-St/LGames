package net.luis.client.players;

import net.luis.client.player.LocalPlayer;
import net.luis.client.player.RemotePlayer;
import net.luis.game.application.FxApplication;
import net.luis.game.player.GameProfile;
import net.luis.game.player.Player;
import net.luis.game.players.AbstractPlayerList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class ClientPlayerList extends AbstractPlayerList {
	
	private static final Logger LOGGER = LogManager.getLogger(ClientPlayerList.class);
	
	private LocalPlayer player;
	
	public ClientPlayerList(@NotNull FxApplication application) {
		super(application);
	}
	
	public @Nullable LocalPlayer getPlayer() {
		return this.player;
	}
	
	//region Adding players
	@Override
	public void addPlayer(@NotNull Player player) {
		super.addPlayer(player);
		if (player instanceof LocalPlayer localPlayer) {
			this.player = localPlayer;
			LOGGER.info("Added local player {}", player.getProfile().getName());
		}
	}
	
	public void addRemotePlayer(@NotNull GameProfile profile) {
		this.players.put(profile, new RemotePlayer(profile));
		LOGGER.info("Added remote player {}", profile.getName());
	}
	//endregion
	
	//region Removing players
	@Override
	public void removePlayer(@NotNull Player player) {
		super.removePlayer(player);
		if (player instanceof LocalPlayer localPlayer) {
			this.player = null;
			LOGGER.info("Removed local player {}", player.getProfile().getName());
		}
	}
	
	public void removePlayer() {
		this.removePlayer(Objects.requireNonNull(this.player));
	}
	
	public void removeRemotePlayer(@NotNull GameProfile profile) {
		this.players.remove(profile);
		LOGGER.info("Removed remote player {}", profile.getName());
	}
	//endregion
}
