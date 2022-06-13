package net.vgc.common.loading;

import java.util.List;

import com.google.common.collect.Lists;

import net.vgc.common.info.Result;

public class LoadingSteps {
	
	protected final List<LoadingStep> loadingSteps;
	
	public LoadingSteps() {
		this.loadingSteps = Lists.newArrayList();
	}
	
	public void load() {
		for (LoadingStep step : this.loadingSteps) {
			if (step.loadingCondition().get()) {
				step.handelSuccess();
			} else {
				step.handelFailed();
				if (!step.canContinueOnFailed()) {
					while (step.getResult() != Result.SUCCESS && !step.isFinished()) {
						
					}
				}
			}
			this.onProgress();
		}
	}
	
	protected void onProgress() {
		
	}
	
}
