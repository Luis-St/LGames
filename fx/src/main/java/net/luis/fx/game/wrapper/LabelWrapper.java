package net.luis.fx.game.wrapper;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-st
 *
 */

public interface LabelWrapper {
	
	@NotNull Label getLabel();
	
	default void setPrefSize(double prefWidth, double prefHeight) {
		this.getLabel().setPrefSize(prefWidth, prefHeight);
	}
	
	default void setFocusTraversable(boolean value) {
		this.getLabel().setFocusTraversable(value);
	}
	
	default void setBackground(@Nullable Background value) {
		this.getLabel().setBackground(value);
	}
	
	default void setGraphic(@Nullable Node value) {
		this.getLabel().setGraphic(value);
	}
}
