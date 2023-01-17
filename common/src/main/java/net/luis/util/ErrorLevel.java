package net.luis.util;

import javafx.scene.paint.Color;
import net.luis.network.buffer.EnumRepresentable;

/**
 *
 * @author Luis-st
 *
 */

public enum ErrorLevel implements EnumRepresentable {
	
	NO("no", 0, Color.BLACK), WARN("warn", 1, Color.YELLOW), ERROR("error", 2, Color.ORANGE), CRITICAL("critical", 3, Color.RED);
	
	private final String name;
	private final int id;
	private final Color color;
	
	ErrorLevel(String name, int id, Color color) {
		this.name = name;
		this.id = id;
		this.color = color;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public int getId() {
		return this.id;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	@Override
	public Enum<ErrorLevel> getDefault() {
		return NO;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
