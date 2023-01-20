package net.luis.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Luis-st
 *
 */

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		LOGGER.warn("Error in thread " + thread.getName(), throwable);
	}
	
}
