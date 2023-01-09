package net.luis.client.screen;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import net.luis.client.Client;
import net.luis.client.fx.ScreenScene;
import net.luis.client.fx.Showable;
import net.luis.language.TranslationKey;
import net.luis.util.Tickable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Luis-st
 *
 */

public abstract class Screen implements Showable, Tickable {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected final Client client;
	public String title = TranslationKey.createAndGet("client.constans.name");
	public int width = 600;
	public int height = 600;
	
	public Screen() {
		this.client = Client.getInstance();
	}
	
	public void init() {
		
	}
	
	@Override
	public void tick() {
		
	}
	
	protected void showScreen(Screen screen) {
		this.client.setScreen(screen);
	}
	
	protected void reapplyScreen() {
		this.client.setScreen(this);
	}
	
	protected abstract Pane createPane();
	
	public final Scene show() {
		Scene scene = new ScreenScene(this.createPane(), this.width, this.height, this);
		this.onSceneShow(scene);
		return scene;
	}
	
	protected void onSceneShow(Scene scene) {
	
	}
	
}
