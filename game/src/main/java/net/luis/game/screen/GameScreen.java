package net.luis.game.screen;

import net.luis.fx.screen.AbstractScreen;
import net.luis.game.Game;
import net.luis.game.application.GameApplication;
import net.luis.game.player.Player;
import net.luis.netcore.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public abstract class GameScreen extends AbstractScreen {
	
	private final Game game;
	
	protected GameScreen(Game game, String title, int width, int height) {
		super(title, width, height, true);
		this.game = Objects.requireNonNull(game, "Game must not be null");
	}
	
	protected GameScreen(Game game, String title, int width, int height, boolean resizable) {
		super(title, width, height, resizable);
		this.game = Objects.requireNonNull(game, "Game must not be null");
	}
	
	public @NotNull Game getGame() {
		return this.game;
	}
	
	public @NotNull Player getPlayer() {
		return this.game.getPlayer().getPlayer();
	}
	
	public void broadcastPlayer(Packet packet) {
		this.game.broadcastPlayer(packet, this.game.getPlayer());
	}
	
	public @NotNull GameApplication getApplication() {
		return this.game.getApplication();
	}
}
