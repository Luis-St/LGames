package net.luis.client.fx;

import javafx.scene.Parent;
import javafx.scene.Scene;
import net.luis.client.screen.Screen;
import net.luis.util.Tickable;

/**
 *
 * @author Luis-st
 *
 */

public class ScreenScene extends Scene implements Tickable {
	
	private final Screen screen;
	
	public ScreenScene(Parent root, double width, double height, Screen screen) {
		super(root, width, height);
		this.screen = screen;
	}
	
	public Screen getScreen() {
		return this.screen;
	}
	
	@Override
	public void tick() {
		this.screen.tick();
	}
	
}
