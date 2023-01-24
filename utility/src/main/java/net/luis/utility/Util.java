package net.luis.utility;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
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
	
}
