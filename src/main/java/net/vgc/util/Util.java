package net.vgc.util;

import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;

import com.google.common.collect.Lists;

import net.vgc.util.logger.LogFileAppender;
import net.vgc.util.logger.ServerConsoleAppender;
import net.vgc.util.streams.DebugPrintStream;
import net.vgc.util.streams.InfoPrintStream;

public class Util {
	
	protected static final List<Appender> APPENDERS = make(Lists.newArrayList(), (list) -> {
		list.add(new ServerConsoleAppender("ServerConsoleAppender"));
		list.add(new LogFileAppender("LatestLogAppender", Level.INFO, Level.FATAL, "latest.log", 25));
		list.add(new LogFileAppender("DebugLogAppender", Level.DEBUG, Level.FATAL, "debug.log", 100));
	});
	protected static final Logger LOGGER = Util.getLogger(Util.class);
	
	public static <T> T make(T object, Consumer<T> consumer) {	
		consumer.accept(object);
		return object;
	}
	
	public static void warpStreams(boolean debugMode) {
		LOGGER.info("Warp System PrintStreams to type {}", debugMode ? "DEBUG" : "INFO");
		if (debugMode) {
			System.setOut(new DebugPrintStream("STDOUT", System.out));
			System.setErr(new DebugPrintStream("STDERR", System.err));
		} else {
			System.setOut(new InfoPrintStream("STDOUT", System.out));
			System.setErr(new InfoPrintStream("STDERR", System.err));
		}
	}
	
	public static Logger getLogger(Class<?> clazz) {
		org.apache.logging.log4j.core.Logger logger = (org.apache.logging.log4j.core.Logger) LogManager.getLogger(clazz);
		for (Appender appender : APPENDERS) {
			if (!appender.isStarted()) {
				appender.start();
			}
			logger.addAppender(appender);
		}
		return logger;
	}
	
}
