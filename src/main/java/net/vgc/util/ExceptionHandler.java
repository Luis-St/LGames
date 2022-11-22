package net.vgc.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		LOGGER.warn("Error in thread " + thread.getName(), throwable);
	}
	
}
