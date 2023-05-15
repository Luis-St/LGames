package net.luis.client.player;

import net.luis.client.Client;
import net.luis.game.player.GameProfile;
import net.luis.game.player.Player;
import net.luis.game.player.score.PlayerScore;
import net.luis.network.Connection;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public class LocalPlayer extends Player {
	
	public LocalPlayer(@NotNull GameProfile profile, @NotNull Connection connection) {
		super(Client.getInstance(), profile, connection, new PlayerScore(profile));
	}
	
	@Override
	public boolean isClient() {
		return true;
	}
	
	@Override
	public @NotNull Client getApplication() {
		return (Client) super.getApplication();
	}
}
