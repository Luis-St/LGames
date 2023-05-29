package net.luis.client.screen;

import net.luis.client.Client;
import net.luis.fx.screen.AbstractScreen;

import java.util.function.Supplier;

/**
 *
 * @author Luis-st
 *
 */

public abstract class ClientScreen extends AbstractScreen {
	
	protected final Supplier<Client> client = Client::getInstance;
	
	protected ClientScreen(String title, int width, int height) {
		super(title, width, height);
	}
	
	protected ClientScreen(String title, int width, int height, boolean resizable) {
		super(title, width, height, resizable);
	}
	
	protected void showScreen(ClientScreen screen) {
		this.client.get().setScreen(screen);
	}
	
	protected void reapplyScreen() {
		this.client.get().setScreen(this);
	}
}
