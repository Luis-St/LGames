package net.luis.game.application;

import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public enum LaunchState {
	
	STARTING("starting"), STARTED("started"), STOPPING("stopping"), STOPPED("stopped"), UNKNOWN("unknown");
	
	private final String name;
	
	LaunchState(@NotNull String name) {
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
