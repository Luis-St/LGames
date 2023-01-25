package net.luis.client.player;

import net.luis.game.player.GameProfile;
import net.luis.game.player.score.PlayerScore;

/**
 *
 * @author Luis-st
 *
 */

public class RemotePlayer extends AbstractClientPlayer {
	
	public RemotePlayer(GameProfile profile) {
		super(profile, new PlayerScore(profile));
	}
	
}
