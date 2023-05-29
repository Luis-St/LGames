package net.luis.client.player;

import net.luis.client.Client;
import net.luis.game.player.GameProfile;
import net.luis.game.player.Player;
import net.luis.game.player.score.PlayerScore;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public class RemotePlayer extends Player {
	
	public RemotePlayer(GameProfile profile) {
		super(Client.getInstance(), profile, new PlayerScore(profile));
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
