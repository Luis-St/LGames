package net.luis.utility;

import javafx.scene.paint.Color;
/**
 *
 * @author Luis-st
 *
 */

public enum ErrorLevel {
	
	NO("no", Color.BLACK), WARN("warn", Color.YELLOW), ERROR("error", Color.ORANGE), CRITICAL("critical", Color.RED);
	
	private final String name;
	private final Color color;
	
	ErrorLevel(String name, Color color) {
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
