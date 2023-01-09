package net.luis.info;

import net.luis.util.EnumRepresentable;

/**
 *
 * @author Luis-st
 *
 */

public enum Result implements EnumRepresentable {
	
	SUCCESS("success", 0), FAILED("failed", 1), UNKNOWN("unknown", 2);
	
	private final String name;
	private final int id;
	
	Result(String name, int id) {
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
	public Enum<Result> getDefault() {
		return UNKNOWN;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
