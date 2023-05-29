package net.luis.utility;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class Util {
	
	public static @NotNull Timeline createTicker(String name, Runnable action) {
		Objects.requireNonNull(action, "Action must not be null");
		Timeline timeline = new Timeline(20.0, new KeyFrame(Duration.millis(50), name, (event) -> action.run()));
		timeline.setCycleCount(Animation.INDEFINITE);
		return timeline;
	}
	
	public static void runDelayed(String name, int millis, Runnable action) {
		Objects.requireNonNull(action, "Action must not be null");
		Timeline timeline = new Timeline(20.0, new KeyFrame(Duration.millis(millis), name, (event) -> action.run()));
		timeline.play();
	}
	
	public static void runCycled(String name, int millis, int cycleCount, Runnable action) {
		Timeline timeline = new Timeline(20.0, new KeyFrame(Duration.millis(millis), name, (event) -> action.run()));
		timeline.setCycleCount(cycleCount);
		timeline.play();
	}
}
