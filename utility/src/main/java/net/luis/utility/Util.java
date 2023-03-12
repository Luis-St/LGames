package net.luis.utility;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public class Util {
	
	public static @NotNull Timeline createTicker(@NotNull String name, @NotNull Runnable action) {
		Timeline timeline = new Timeline(20.0, new KeyFrame(Duration.millis(50), name, (event) -> action.run()));
		timeline.setCycleCount(Animation.INDEFINITE);
		return timeline;
	}
	
	public static void runDelayed(@NotNull String name, int millis, @NotNull Runnable action) {
		Timeline timeline = new Timeline(20.0, new KeyFrame(Duration.millis(millis), name, (event) -> action.run()));
		timeline.play();
	}
	
	public static void runCycled(@NotNull String name, int millis, int cycleCount, @NotNull Runnable action) {
		Timeline timeline = new Timeline(20.0, new KeyFrame(Duration.millis(millis), name, (event) -> action.run()));
		timeline.setCycleCount(cycleCount);
		timeline.play();
	}
	
}
