package net.luis.fx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public class Box<T extends Node> extends VBox {
	
	private final T node;
	private final Pos pos;
	private final Insets padding;
	
	public Box(@NotNull T node) {
		this(node, Pos.CENTER);
	}
	
	public Box(@NotNull T node, @NotNull Pos pos) {
		this(node, pos, 0.0, 0.0, 0.0, 0.0);
	}
	
	public Box(@NotNull T node, @NotNull Pos pos, double top, double right, double bottom, double left) {
		this(node, pos, new Insets(top, right, bottom, left));
	}
	
	public Box(@NotNull T node, @NotNull Pos pos, @NotNull Insets padding) {
		this.node = node;
		this.pos = pos;
		this.padding = padding;
		this.init();
	}
	
	protected void init() {
		this.setAlignment(this.pos);
		this.setPadding(this.padding);
		this.getChildren().add(this.node);
	}
	
	public @NotNull T getNode() {
		return this.node;
	}
	
}
