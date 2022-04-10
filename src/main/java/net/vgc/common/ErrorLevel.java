package net.vgc.common;

import javafx.scene.paint.Color;

public enum ErrorLevel {
	
	NON("non", Color.BLACK),
	WARN("warn", Color.YELLOW),
	ERROR("error", Color.ORANGE),
	CRITICAL("critical", Color.RED);
	
	private final String name;
	private final Color color;
	
	private ErrorLevel(String name, Color color) {
		this.name = name;
		this.color = color;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
