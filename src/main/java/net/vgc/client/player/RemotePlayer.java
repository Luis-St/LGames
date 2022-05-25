package net.vgc.client.player;

import net.vgc.oldgame.score.PlayerScore;
import net.vgc.player.GameProfile;

public class RemotePlayer extends AbstractClientPlayer {

	public RemotePlayer(GameProfile profile) {
		super(profile, new PlayerScore(profile));
	}

}
