package net.vgc.client.player;

import net.vgc.game.score.PlayerScore;
import net.vgc.player.GameProfile;

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
