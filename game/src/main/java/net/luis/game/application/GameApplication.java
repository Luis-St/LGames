package net.luis.game.application;

import net.luis.fx.screen.AbstractScreen;
import net.luis.game.GameManager;
import net.luis.game.players.PlayerList;
import net.luis.game.resources.ResourceManager;
import net.luis.network.instance.NetworkInstance;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public interface GameApplication {
	
	@NotNull ApplicationType getType();
	
	@NotNull NetworkInstance getNetworkInstance();
	
	@NotNull ResourceManager getResourceManager();
	
	@NotNull PlayerList getPlayerList();
	
	@NotNull GameManager getGameManager();
	
	@NotNull AbstractScreen getScreen();
	
	void setScreen(@NotNull AbstractScreen screen);
	
}
