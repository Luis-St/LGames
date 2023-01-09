package net.luis.util.streams;

import java.io.OutputStream;

/**
 *
 * @author Luis-st
 *
 */

public class DebugPrintStream extends InfoPrintStream {
	
	public DebugPrintStream(String name, OutputStream stream) {
		super(name, stream);
	}
	
	@Override
	protected void log(String string) {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		StackTraceElement stackTraceElement = stackTraceElements[Math.min(3, stackTraceElements.length)];
		LOGGER.info("[{}/{}:{}] {}", this.name, stackTraceElement.getFileName(), stackTraceElement.getLineNumber(), string);
	}
	
}
