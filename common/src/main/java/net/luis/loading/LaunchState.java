package net.luis.loading;

import net.luis.network.buffer.EnumRepresentable;

/**
 *
 * @author Luis-st
 *
 */

public enum LaunchState implements EnumRepresentable {
	
	STARTING("starting", 0), STARTED("started", 1), STOPPING("stopping", 2), STOPPED("stopped", 3), UNKNOWN("unknown", 4);
	
	private final String name;
	private final int id;
	
	LaunchState(String name, int id) {
		this.name = name;
		this.id = id;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public int getId() {
		return this.id;
	}
	
	@Override
	public Enum<LaunchState> getDefault() {
		return UNKNOWN;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
