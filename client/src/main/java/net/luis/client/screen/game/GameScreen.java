package net.luis.client.screen.game;

import net.luis.client.player.LocalPlayer;
import net.luis.client.screen.ClientScreen;

/**
 *
 * @author Luis-st
 *
 */

public abstract class GameScreen extends ClientScreen {
	
	protected GameScreen(String title, int width, int height) {
		super(title, width, height);
	}
	
	protected LocalPlayer getPlayer() {
		return this.client.getPlayer();
	}
	
}
