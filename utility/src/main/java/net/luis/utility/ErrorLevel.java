package net.luis.utility;

import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public enum ErrorLevel {
	
	NO("no", Color.BLACK), WARN("warn", Color.YELLOW), ERROR("error", Color.ORANGE), CRITICAL("critical", Color.RED);
	
	private final String name;
	private final Color color;
	
	ErrorLevel(@NotNull String name, @NotNull Color color) {
		this.name = name;
		this.color = color;
	}
	
	public @NotNull String getName() {
		return this.name;
	}
	
	public @NotNull Color getColor() {
		return this.color;
	}
	
	@Override
	public @NotNull String toString() {
		return this.name;
	}
}
