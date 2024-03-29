package net.vgc.client.fx.game.wrapper;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;

/**
 *
 * @author Luis-st
 *
 */

public interface ToggleButtonWrapper {
	
	ToggleButton getToggleButton();
	
	default ToggleGroup getToggleGroup() {
		return this.getToggleButton().getToggleGroup();
	}
	
	default void setToggleGroup(ToggleGroup group) {
		this.getToggleButton().setToggleGroup(group);
	}
	
	default void setPrefSize(double prefWidth, double prefHeight) {
		this.getToggleButton().setPrefSize(prefWidth, prefHeight);
	}
	
	default void setFocusTraversable(boolean value) {
		this.getToggleButton().setFocusTraversable(value);
	}
	
	default void setBackground(Background value) {
		this.getToggleButton().setBackground(value);
	}
	
	default void setGraphic(Node value) {
		this.getToggleButton().setGraphic(value);
	}
	
	default void setOnAction(EventHandler<ActionEvent> value) {
		this.getToggleButton().setOnAction(value);
	}
	
	default void setSelected(boolean value) {
		this.getToggleButton().setSelected(value);
	}
	
}
