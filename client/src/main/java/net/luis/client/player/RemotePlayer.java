package net.luis.client.player;

import net.luis.common.player.GameProfile;
import net.luis.game.score.PlayerScore;

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
