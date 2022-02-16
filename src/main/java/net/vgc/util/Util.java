package net.project.util;

import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.project.util.streams.DebugPrintStream;
import net.project.util.streams.InfoPrintStream;

public class Util {
	
	protected static final Logger LOGGER = LogManager.getLogger(Util.class);
	
	public static <T> T make(T object, Consumer<T> consumer) {
		consumer.accept(object);
		return object;
	}
	
	public static void warpStreams(boolean debugMode) {
		LOGGER.info("Warp System PrintStreams to type {}", debugMode ? "DEBUG" : "INFO");
		if (debugMode) {
			System.setOut(new DebugPrintStream("STDOUT", System.out));
		} else {
			System.setOut(new InfoPrintStream("STDOUT", System.out));
		}
	}
	
}
