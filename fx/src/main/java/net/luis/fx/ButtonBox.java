package net.luis.fx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import net.luis.fxutils.EventHandlers;

/**
 *
 * @author Luis-st
 *
 */

public class ButtonBox extends Box<Button> {
	
	private final Runnable action;
	
	public ButtonBox(String text, Runnable action) {
		this(text, Pos.CENTER, action);
	}
	
	public ButtonBox(String text, Pos pos, Runnable action) {
		this(text, Pos.CENTER, 0.0, 0.0, 0.0, 0.0, action);
	}
	
	public ButtonBox(String text, Pos pos, double padding, Runnable action) {
		super(new Button(text), pos, new Insets(padding, padding, padding, padding));
		this.action = action;
	}
	
	public ButtonBox(String text, Pos pos, double top, double right, double bottom, double left, Runnable action) {
		super(new Button(text), pos, new Insets(top, right, bottom, left));
		this.action = action;
	}
	
	public ButtonBox(String text, Pos pos, Insets padding, Runnable action) {
		super(new Button(text), pos, padding);
		this.action = action;
	}
	
	@Override
	protected void init() {
		super.init();
		this.getNode().setOnAction(EventHandlers.create(this.action));
	}
	
}
