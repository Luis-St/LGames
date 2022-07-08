package net.vgc.server.game.dice;

import java.util.Random;

import net.vgc.util.Mth;
import net.vgc.util.exception.InvalidValueException;

public class SimpleDice implements Dice {
	
	private final int min;
	private final int max;
	private final Random rng;
	
	public SimpleDice(int min, int max) {
		this(min, max, new Random());
	}
	
	public SimpleDice(int min, int max, Random rng) {
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
		return Mth.randomInt(this.rng, this.min, this.max);
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
	public boolean equals(Object object) {
		if (object instanceof SimpleDice dice) {
			if (!this.rng.equals(object)) {
				return false;
			} else if (this.min != dice.min) {
				return false;
			} else {
				return this.max == dice.max;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("SimpleDice{");
		builder.append("min=").append(this.min).append(",");
		builder.append("max=").append(this.max).append("}");
		return builder.toString();
	}

}
