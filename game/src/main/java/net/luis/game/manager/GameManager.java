package net.luis.game.manager;

import net.luis.game.Game;
import net.luis.game.player.GameProfile;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-st
 *
 */

public class GameManager {
	
	private Game game;
	private GameProfile localProfile;
	
	@Nullable
	public Game getGame() {
		return this.game;
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	@Nullable
	public GameProfile getLocalProfile() {
		return this.localProfile;
	}
	
	public void setLocalProfile(GameProfile localProfile) {
		this.localProfile = localProfile;
	}
}
