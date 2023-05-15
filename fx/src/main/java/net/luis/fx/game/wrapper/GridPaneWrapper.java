package net.luis.fx.game.wrapper;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public interface GridPaneWrapper {
	
	@NotNull GridPane getGridPane();
	
	default void setAlignment(@NotNull Pos value) {
		this.getGridPane().setAlignment(value);
	}
	
	default void setHgap(double value) {
		this.getGridPane().setHgap(value);
	}
	
	default void setVgap(double value) {
		this.getGridPane().setVgap(value);
	}
	
	default void setPadding(@NotNull Insets value) {
		this.getGridPane().setPadding(value);
	}
	
	default void setGridLinesVisible(boolean value) {
		this.getGridPane().setGridLinesVisible(value);
	}
	
	default void add(@NotNull Node child, int columnIndex, int rowIndex) {
		this.getGridPane().add(child, columnIndex, rowIndex);
	}
	
	default void addColumn(int columnIndex, @NotNull Node... children) {
		this.getGridPane().addColumn(columnIndex, children);
	}
	
	default void addRow(int rowIndex, @NotNull Node... children) {
		this.getGridPane().addRow(rowIndex, children);
	}
}
