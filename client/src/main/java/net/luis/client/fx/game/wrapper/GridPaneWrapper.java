package net.luis.client.fx.game.wrapper;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 *
 * @author Luis-st
 *
 */

public interface GridPaneWrapper {
	
	GridPane getGridPane();
	
	default void setAlignment(Pos value) {
		this.getGridPane().setAlignment(value);
	}
	
	default void setHgap(double value) {
		this.getGridPane().setHgap(value);
	}
	
	default void setVgap(double value) {
		this.getGridPane().setVgap(value);
	}
	
	default void setPadding(Insets value) {
		this.getGridPane().setPadding(value);
	}
	
	default void setGridLinesVisible(boolean value) {
		this.getGridPane().setGridLinesVisible(value);
	}
	
	default void add(Node child, int columnIndex, int rowIndex) {
		this.getGridPane().add(child, columnIndex, rowIndex);
	}
	
	default void addColumn(int columnIndex, Node... children) {
		this.getGridPane().addColumn(columnIndex, children);
	}
	
	default void addRow(int rowIndex, Node... children) {
		this.getGridPane().addRow(rowIndex, children);
	}
	
}
