package net.luis.game.dice;

import net.luis.utils.exception.InvalidValueException;
import net.luis.utils.math.Mth;
import net.luis.utils.util.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Random;

/**
 *
 * @author Luis-st
 *
 */

public class SimpleDice implements Dice {
	
	private static final Logger LOGGER = LogManager.getLogger(SimpleDice.class);
	
	private final int min;
	private final int max;
	private final Random rng;
	
	public SimpleDice(int min, int max) {
		this(min, max, new Random());
	}
	
	public SimpleDice(int min, int max, @NotNull Random rng) {
		this.min = min;
		this.max = max;
		this.rng = rng;
	}
	
	@Override
	public int roll() {
		if (this.min > this.max) {
			LOGGER.error("The min value {} of the dice can not be larger than the max value {}", this.min, this.max);
			throw new InvalidValueException("The min value " + this.min + "of the dice can not be larger than the max value " + this.max);
		} else if (this.min == this.max) {
			return this.min;
		} else if (this.min == 0) {
			return this.rng.nextInt(this.max) + 1;
		}
		return Mth.randomInclusiveInt(this.rng, this.min, this.max);
	}
	
	@Override
	public int rollExclude(int value) {
		if (!Mth.isInBounds(value, this.min, this.max)) {
			LOGGER.warn("The exclude value must be in bounds {} - {} but it is {}", this.min, this.max, value);
			return this.roll();
		}
		int count = this.roll();
		while (count == value) {
			count = this.roll();
			if (count != value) {
				break;
			}
		}
		return count;
	}
	
	@Override
	public int rollPreferred(int value, int rolls) {
		if (0 >= rolls) {
			LOGGER.warn("The roll count must be larger than 0");
			return this.roll();
		}
		if (rolls > this.max) {
			LOGGER.warn("The roll count can not be larger than the max dice count {} but it is {}", this.max, rolls);
			return this.roll();
		}
		int count = this.roll();
		for (int i = 0; i < rolls - 1; i++) {
			if (count == value) {
				break;
			}
			count = this.roll();
		}
		return count;
	}
	
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (!(o instanceof SimpleDice that)) return false;
		
		if (this.min != that.min) return false;
		return this.max == that.max;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.min, this.max);
	}
	
	@Override
	public @NotNull String toString() {
		return ToString.toString(this, "rng");
	}
	
}
