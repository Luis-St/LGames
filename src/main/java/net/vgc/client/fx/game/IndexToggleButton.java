package net.vgc.client.fx.game;

import javafx.scene.control.ToggleButton;

/**
 *
 * @author Luis-st
 *
 */

public class IndexToggleButton extends ToggleButton {
	
	private final int index;
	
	public IndexToggleButton(int index) {
		this.index = index;
	}
	
	public IndexToggleButton(String text, int index) {
		super(text);
		this.index = index;
	}
	
	public int getIndex() {
		return this.index;
	}
	
}
