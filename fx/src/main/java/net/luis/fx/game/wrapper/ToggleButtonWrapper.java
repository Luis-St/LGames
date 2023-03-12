package net.luis.fx.game.wrapper;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-st
 *
 */

public interface ToggleButtonWrapper {
	
	@NotNull
	ToggleButton getToggleButton();
	
	default @NotNull ToggleGroup getToggleGroup() {
		return this.getToggleButton().getToggleGroup();
	}
	
	default void setToggleGroup(@NotNull ToggleGroup group) {
		this.getToggleButton().setToggleGroup(group);
	}
	
	default void setPrefSize(double prefWidth, double prefHeight) {
		this.getToggleButton().setPrefSize(prefWidth, prefHeight);
	}
	
	default void setFocusTraversable(boolean value) {
		this.getToggleButton().setFocusTraversable(value);
	}
	
	default void setBackground(@Nullable Background value) {
		this.getToggleButton().setBackground(value);
	}
	
	default void setGraphic(@Nullable Node value) {
		this.getToggleButton().setGraphic(value);
	}
	
	default void setOnAction(@NotNull EventHandler<ActionEvent> value) {
		this.getToggleButton().setOnAction(value);
	}
	
	default void setSelected(boolean value) {
		this.getToggleButton().setSelected(value);
	}
	
}
