package net.luis.fx;

import javafx.scene.Parent;
import javafx.scene.Scene;
import net.luis.fx.screen.AbstractScreen;
import net.luis.utility.Tickable;

/**
 *
 * @author Luis-st
 *
 */

public class ScreenScene extends Scene implements Tickable {
	
	private final AbstractScreen screen;
	
	public ScreenScene(Parent root, double width, double height, AbstractScreen screen) {
		super(root, width, height);
		this.screen = screen;
	}
	
	public AbstractScreen getScreen() {
		return this.screen;
	}
	
	@Override
	public void tick() {
		this.screen.tick();
	}
	
}
