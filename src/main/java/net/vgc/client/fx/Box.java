package net.vgc.client.fx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class Box<T extends Node> extends VBox {
	
	private final T node;
	private final Pos pos;
	private final Insets padding;
	
	public Box(T node) {
		this(node, Pos.CENTER);
	}
	
	public Box(T node, Pos pos) {
		this(node, Pos.CENTER, 0.0, 0.0, 0.0, 0.0);
	}
	
	public Box(T node, Pos pos, double top, double right, double bottom, double left) {
		this(node, pos, new Insets(top, right, bottom, left));
	}
	
	public Box(T node, Pos pos, Insets padding) {
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
	
	public T getNode() {
		return this.node;
	}
	
}
