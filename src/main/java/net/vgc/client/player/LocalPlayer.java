package net.vgc.client.player;

import net.vgc.oldgame.score.PlayerScore;
import net.vgc.player.GameProfile;

public class LocalPlayer extends AbstractClientPlayer {

	public LocalPlayer(GameProfile profile) {
		super(profile, new PlayerScore(profile));
	}

}
