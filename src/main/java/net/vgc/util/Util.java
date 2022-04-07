package net.vgc.util;

import java.util.UUID;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import net.vgc.Constans;
import net.vgc.util.streams.DebugPrintStream;
import net.vgc.util.streams.InfoPrintStream;

public class Util {
	
	protected static final Logger LOGGER = LogManager.getLogger(Util.class);
	public static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	
	public static <T> T make(T object, Consumer<T> consumer) {	
		consumer.accept(object);
		return object;
	}
	
	public static Timeline createTicker(String name, Runnable action) {
		Timeline timeline = new Timeline(20.0, new KeyFrame(Duration.millis(50), name, (event) -> {
			action.run();
		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		return timeline;
	}
	
	public static void runDelayed(String name, int millis, Runnable action) {
		Timeline timeline = new Timeline(20.0, new KeyFrame(Duration.millis(millis), name, (event) -> {
			action.run();
		}));
		timeline.play();
	}
	
	public static void runCycled(String name, int millis, int cycleCount, Runnable action) {
		Timeline timeline = new Timeline(20.0, new KeyFrame(Duration.millis(millis), name, (event) -> {
			action.run();
		}));
		timeline.setCycleCount(cycleCount);
		timeline.play();
	}
	
	public static void runAfter(String name, int delay, Runnable action) {
		Timeline timeline = new Timeline(20.0, new KeyFrame(Duration.millis(0), name, (event) -> {
			action.run();
		}));
		timeline.setDelay(Duration.millis(delay));
		timeline.play();
	}
	
	public static void warpStreams(boolean debugMode) {
		LOGGER.info("Warp System PrintStreams to type {}", debugMode ? "DEBUG" : "INFO");
		Constans.DEBUG = debugMode;
		if (debugMode) {
			System.setOut(new DebugPrintStream("STDOUT", System.out));
			System.setErr(new DebugPrintStream("STDERR", System.err));
		} else {
			System.setOut(new InfoPrintStream("STDOUT", System.out));
			System.setErr(new InfoPrintStream("STDERR", System.err));
		}
	}
	
}
