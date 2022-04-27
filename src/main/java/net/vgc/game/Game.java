package net.vgc.game;

import net.vgc.client.screen.game.GameScreen;

public interface Game {
	
	int getMinPlayers();
	
	int getMaxPlayers();
	
	GameScreen getScreen();
	
	String getName();
	
}
