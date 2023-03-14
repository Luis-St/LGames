package net.luis.game.screen;

import net.luis.fx.screen.AbstractScreen;
import net.luis.game.Game;
import net.luis.game.application.GameApplication;
import net.luis.game.player.Player;
import net.luis.network.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public abstract class GameScreen extends AbstractScreen {
	
	private final Game game;
	
	protected GameScreen(@NotNull Game game, @NotNull String title, int width, int height) {
		super(title, width, height, true);
		this.game = game;
	}
	
	protected GameScreen(@NotNull Game game, @NotNull String title, int width, int height, boolean resizable) {
		super(title, width, height, resizable);
		this.game = game;
	}
	
	public @NotNull Game getGame() {
		return this.game;
	}
	
	public @NotNull Player getPlayer() {
		return Objects.requireNonNull(this.game.getPlayer()).getPlayer();
	}
	
	public void broadcastPlayer(@NotNull Packet packet) {
		this.game.broadcastPlayer(packet, Objects.requireNonNull(this.game.getPlayer()));
	}
	
	public @NotNull GameApplication getApplication() {
		return this.game.getApplication();
	}
	
}
