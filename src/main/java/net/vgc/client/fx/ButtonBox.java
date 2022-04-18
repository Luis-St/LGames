package net.vgc.client.fx;

import javafx.geometry.Pos;
import javafx.scene.control.Button;

public class ButtonBox extends Box<Button> {
	
	protected final Runnable action;
	
	public ButtonBox(String text, Runnable action) {
		this(text, Pos.CENTER, action);
	}
	
	public ButtonBox(String text, Pos pos, Runnable action) {
		super(new Button(text), pos);
		this.action = action;
	}
	
	@Override
	protected void init() {
		super.init();
		this.node.setOnAction((event) -> {
			this.action.run();
		});
	}

}
