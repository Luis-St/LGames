package net.luis.fx;

import javafx.scene.Parent;
import javafx.scene.Scene;
import net.luis.fx.screen.AbstractScreen;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class ScreenScene extends Scene {
	
	private final AbstractScreen screen;
	
	public ScreenScene(Parent root, double width, double height, AbstractScreen screen) {
		super(root, width, height);
		this.screen = Objects.requireNonNull(screen, "Screen must not be null");
	}
	
	public @NotNull AbstractScreen getScreen() {
		return this.screen;
	}
}
