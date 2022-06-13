package net.vgc.common.loading;

import java.util.function.Supplier;

import net.vgc.common.info.Result;

public class LoadingStep {
	
	protected Result result;
	
	public LoadingStep() {
		
	}
	
	public Supplier<Boolean> loadingCondition() {
		return () -> false;
	}
	
	public void handelSuccess() {
		
	}
	
	public void handelFailed() {
		
	}
	
	public boolean canContinueOnFailed() {
		return false;
	}
	
	public void setResult(Result result) {
		this.result = result;
	}
	
	public Result getResult() {
		return this.result;
	}
	
	public boolean isFinished() {
		return false;
	}
	
}
