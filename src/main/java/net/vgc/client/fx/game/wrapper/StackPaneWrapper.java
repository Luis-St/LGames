package net.vgc.client.fx.game.wrapper;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Luis-st
 *
 */

public interface StackPaneWrapper {
	
	StackPane getStackPane();
	
	default void setAlignment(Pos value) {
		this.getStackPane().setAlignment(value);
	}
	
	default void setPadding(Insets value) {
		this.getStackPane().setPadding(value);
	}
	
	default ObservableList<Node> getChildren() {
		return this.getStackPane().getChildren();
	}
	
}
