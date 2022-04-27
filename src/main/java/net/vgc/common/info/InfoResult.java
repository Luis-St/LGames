package net.vgc.common.info;

public record InfoResult(Result result, String info) {
	
	public static final InfoResult EMPTY = new InfoResult(Result.UNKNOWN, "");
	
	public boolean isSuccess() {
		return this.result == Result.SUCCESS;
	}
	
}
