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

public class RemotePlayer extends Player {
	
	public RemotePlayer(@NotNull GameProfile profile) {
		super(Client.getInstance(), profile, null, new PlayerScore(profile));
	}
	
	@Override
	public boolean isClient() {
		return true;
	}
	
	@Override
	public @NotNull Client getApplication() {
		return (Client) super.getApplication();
	}
	
	@Override
	public @NotNull Connection getConnection() {
		throw new IllegalStateException("RemotePlayer does not have a connection");
	}
}
