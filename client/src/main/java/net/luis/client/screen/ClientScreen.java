package net.luis.client.screen;

import net.luis.client.Client;
import net.luis.fx.Showable;
import net.luis.fx.screen.AbstractScreen;
import net.luis.utility.Tickable;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public abstract class ClientScreen extends AbstractScreen implements Showable, Tickable {
	
	protected final Client client = Objects.requireNonNull(Client.getInstance());
	
	protected ClientScreen(String title, int width, int height) {
		super(title, width, height);
	}
	
	public void init() {
	
	}
	
	@Override
	public void tick() {
	
	}
	
	protected void showScreen(ClientScreen screen) {
		this.client.setScreen(screen);
	}
	
	protected void reapplyScreen() {
		this.client.setScreen(this);
	}
}