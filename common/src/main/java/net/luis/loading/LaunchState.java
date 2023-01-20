package net.luis.loading;

/**
 *
 * @author Luis-st
 *
 */

public enum LaunchState {
	
	STARTING("starting"), STARTED("started"), STOPPING("stopping"), STOPPED("stopped"), UNKNOWN("unknown");
	
	private final String name;
	
	LaunchState(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
