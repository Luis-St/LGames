package net.luis.common.util;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import net.luis.common.util.streams.DebugPrintStream;
import net.luis.common.util.streams.InfoPrintStream;
import net.luis.common.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Luis-st
 *
 */

public class Util {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static Timeline createTicker(String name, Tickable tickable) {
		Timeline timeline = new Timeline(20.0, new KeyFrame(Duration.millis(50), name, (event) -> tickable.tick()));
		timeline.setCycleCount(Animation.INDEFINITE);
		return timeline;
	}
	
	public static void runDelayed(String name, int millis, Runnable action) {
		Timeline timeline = new Timeline(20.0, new KeyFrame(Duration.millis(millis), name, (event) -> action.run()));
		timeline.play();
	}
	
	public static void runCycled(String name, int millis, int cycleCount, Runnable action) {
		Timeline timeline = new Timeline(20.0, new KeyFrame(Duration.millis(millis), name, (event) -> action.run()));
		timeline.setCycleCount(cycleCount);
		timeline.play();
	}
	
	public static void warpStreams(boolean debugMode) {
		LOGGER.info("Warp System PrintStreams to type {}", debugMode ? "DEBUG" : "INFO");
		Constants.DEBUG = debugMode;
		if (debugMode) {
			System.setOut(new DebugPrintStream("STDOUT", System.out));
			System.setErr(new DebugPrintStream("STDERR", System.err));
		} else {
			System.setOut(new InfoPrintStream("STDOUT", System.out));
			System.setErr(new InfoPrintStream("STDERR", System.err));
		}
	}
	
}
