package net.luis.fx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import net.luis.fxutils.EventHandlers;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public class ButtonBox extends Box<Button> {
	
	private final Runnable action;
	
	public ButtonBox(@NotNull String text, @NotNull Runnable action) {
		this(text, Pos.CENTER, action);
	}
	
	public ButtonBox(@NotNull String text, @NotNull Pos pos, @NotNull Runnable action) {
		this(text, Pos.CENTER, 0.0, 0.0, 0.0, 0.0, action);
	}
	
	public ButtonBox(@NotNull String text, @NotNull Pos pos, double padding, @NotNull Runnable action) {
		super(new Button(text), pos, new Insets(padding, padding, padding, padding));
		this.action = action;
	}
	
	public ButtonBox(@NotNull String text, @NotNull Pos pos, double top, double right, double bottom, double left, @NotNull Runnable action) {
		super(new Button(text), pos, new Insets(top, right, bottom, left));
		this.action = action;
	}
	
	public ButtonBox(@NotNull String text, @NotNull Pos pos, @NotNull Insets padding, @NotNull Runnable action) {
		super(new Button(text), pos, padding);
		this.action = action;
	}
	
	@Override
	protected void init() {
		super.init();
		this.getNode().setOnAction(EventHandlers.create(this.action));
	}
	
}
