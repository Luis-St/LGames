package net.vgc.client.screen.game;

import net.vgc.client.player.LocalPlayer;
import net.vgc.client.screen.Screen;

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
