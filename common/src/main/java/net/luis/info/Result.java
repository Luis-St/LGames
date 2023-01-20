package net.luis.info;

/**
 *
 * @author Luis-st
 *
 */

public enum Result {
	
	SUCCESS("success"), FAILED("failed"), UNKNOWN("unknown");
	
	private final String name;
	
	Result(String name) {
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
