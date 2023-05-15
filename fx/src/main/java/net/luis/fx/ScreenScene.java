package net.luis.fx;

import javafx.scene.Parent;
import javafx.scene.Scene;
import net.luis.fx.screen.AbstractScreen;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public class ScreenScene extends Scene {
	
	private final AbstractScreen screen;
	
	public ScreenScene(@NotNull Parent root, double width, double height, @NotNull AbstractScreen screen) {
		super(root, width, height);
		this.screen = screen;
	}
	
	public @NotNull AbstractScreen getScreen() {
		return this.screen;
	}
}
