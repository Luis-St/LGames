package net.luis.game;

import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public enum GameResult {
	
	WIN("win"), LOSE("lose"), DRAW("draw"), NO("no");
	
	private final String name;
	
	GameResult(@NotNull String name) {
		this.name = name;
	}
	
	public @NotNull String getName() {
		return this.name;
	}
	
	@Override
	public @NotNull String toString() {
		return this.name;
	}
}
