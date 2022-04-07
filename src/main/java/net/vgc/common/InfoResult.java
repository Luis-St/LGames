package net.vgc.common;

public record InfoResult(Result result, String info) {
	
	public static final InfoResult EMPTY = new InfoResult(Result.UNKNOWN, "");
	
	public boolean isSuccess() {
		return this.result == Result.SUCCESS;
	}
	
}
