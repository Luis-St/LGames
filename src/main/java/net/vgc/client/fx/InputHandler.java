package net.vgc.client.fx;

import javafx.scene.input.KeyCode;
import net.vgc.client.fx.input.InputContext;
import net.vgc.client.fx.input.MouseInputContext;
import net.vgc.client.fx.input.ScrollInputContext;

public interface InputHandler {
	
	default void keyPressed(KeyCode key, InputContext context) {
		
	}
	
	default void keyReleased(KeyCode key, InputContext context) {
		
	}
	
	default void keyTyped(KeyCode key, String character, InputContext context) {
		
	}
	
	default void mouseClicked(MouseInputContext context) {
		
	}
	
	default void mouseEntered(MouseInputContext context) {
		
	}
	
	default void mouseExited(MouseInputContext context) {
		
	}
	
	default void scrollStarted(ScrollInputContext context) {
		
	}
	
	default void scrollFinished(ScrollInputContext context) {
		
	}
	
}
