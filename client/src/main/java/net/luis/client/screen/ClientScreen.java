package net.luis.client.screen;

import net.luis.client.Client;
import net.luis.fx.screen.AbstractScreen;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public abstract class ClientScreen extends AbstractScreen {
	
	protected final Client client = Client.getInstance();
	
	protected ClientScreen(@NotNull String title, int width, int height) {
		super(title, width, height);
	}
	
	protected ClientScreen(@NotNull String title, int width, int height, boolean resizable) {
		super(title, width, height, resizable);
	}
	
	public void init() {
	
	}
	
	protected void showScreen(@NotNull ClientScreen screen) {
		this.client.setScreen(screen);
	}
	
	protected void reapplyScreen() {
		this.client.setScreen(this);
	}
}
