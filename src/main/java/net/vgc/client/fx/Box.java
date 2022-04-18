package net.vgc.client.fx;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class Box<T extends Node> extends VBox {
	
	protected final T node;
	protected final Pos pos;
	
	public Box(T node) {
		this(node, Pos.CENTER);
	}
	
	public Box(T node, Pos pos) {
		this.node = node;
		this.pos = pos;
		this.init();
	}
	
	protected void init() {
		this.setAlignment(this.pos);
		this.getChildren().add(this.node);
	}
	
	public T getNode() {
		return this.node;
	}
	
}
