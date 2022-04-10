package net.vgc.common;

import javax.annotation.Nullable;

public enum Result {
	
	SUCCESS("success"),
	FAILED("failed"),
	UNKNOWN("unknown");

	private final String name;
	
	private Result(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	@Nullable
	public static Result fromName(String name) {
		for (Result result : Result.values()) {
			if (result.name().equals(name)) {
				return result;
			}
		}
		return UNKNOWN;
	}
	
}
