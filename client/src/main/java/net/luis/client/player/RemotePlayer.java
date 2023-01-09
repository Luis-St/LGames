package net.luis.client.player;

import net.luis.game.score.PlayerScore;
import net.luis.player.GameProfile;

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
