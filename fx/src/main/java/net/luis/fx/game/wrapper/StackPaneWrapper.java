package net.luis.fx.game.wrapper;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public interface StackPaneWrapper {
	
	@NotNull StackPane getStackPane();
	
	default void setAlignment(Pos value) {
		this.getStackPane().setAlignment(value);
	}
	
	default void setPadding(Insets value) {
		this.getStackPane().setPadding(value);
	}
	
	default @NotNull ObservableList<Node> getChildren() {
		return this.getStackPane().getChildren();
	}
}
