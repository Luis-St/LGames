package net.luis.client.fx.game.wrapper;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;

/**
 *
 * @author Luis-st
 *
 */

public interface LabelWrapper {
	
	Label getLabel();
	
	default void setPrefSize(double prefWidth, double prefHeight) {
		this.getLabel().setPrefSize(prefWidth, prefHeight);
	}
	
	default void setFocusTraversable(boolean value) {
		this.getLabel().setFocusTraversable(value);
	}
	
	default void setBackground(Background value) {
		this.getLabel().setBackground(value);
	}
	
	default void setGraphic(Node value) {
		this.getLabel().setGraphic(value);
	}
	
}
