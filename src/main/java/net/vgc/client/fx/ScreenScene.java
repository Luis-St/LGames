package net.vgc.client.fx;

import javafx.scene.Parent;
import javafx.scene.Scene;
import net.vgc.client.screen.Screen;
import net.vgc.util.Tickable;

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
