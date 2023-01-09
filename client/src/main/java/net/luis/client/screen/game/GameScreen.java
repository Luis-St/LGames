package net.luis.client.screen.game;

import net.luis.client.player.LocalPlayer;
import net.luis.client.screen.Screen;

/**
 *
 * @author Luis-st
 *
 */

public abstract class GameScreen extends Screen {
	
	public GameScreen() {
		
	}
	
	protected LocalPlayer getPlayer() {
		return this.client.getPlayer();
	}
	
}
