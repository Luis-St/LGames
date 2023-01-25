package net.luis.fx.screen;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import net.luis.fx.ScreenScene;
import net.luis.fx.Showable;
import net.luis.utility.Tickable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Luis-st
 *
 */

public abstract class AbstractScreen implements Showable, Tickable {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	public final String title;
	public final int width;
	public final int height;
	
	protected AbstractScreen(String title, int width, int height) {
		this.title = title;
		this.width = width;
		this.height = height;
	}
	
	public void init() {
		
	}
	
	@Override
	public void tick() {
		
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
