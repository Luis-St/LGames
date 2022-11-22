package net.vgc.common.loading;

import net.vgc.util.EnumRepresentable;

public enum LaunchState implements EnumRepresentable {
	
	STARTING("starting", 0), STARTED("started", 1), STOPPING("stopping", 2), STOPPED("stopped", 3), UNKNOWN("unknown", 4);
	
	private final String name;
	private final int id;
	
	private LaunchState(String name, int id) {
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
