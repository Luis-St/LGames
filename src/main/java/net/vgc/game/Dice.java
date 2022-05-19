package net.vgc.game;

import java.util.Random;

import net.vgc.Main;
import net.vgc.util.Mth;
import net.vgc.util.Util;
import net.vgc.util.exception.InvalidValueException;

public class Dice {
	
	protected final Random rng;
	protected final int min;
	protected final int max;
	
	public Dice(int min, int max) {
		this(Util.systemRandom(), min, max);
	}
	
	public Dice(Random rng, int min, int max) {
		this.rng = rng;
		this.min = min;
		this.max = max;
	}
	
	public int roll() {
		if (this.min > this.max) {
			Main.LOGGER.error("The min value {} of the dice can not be larger than the max value {}", this.min, this.max);
			throw new InvalidValueException("The min value " + this.min + "of the dice can not be larger than the max value " + this.max);
		} else if (this.min == this.max) {
			return this.min;
		} else if (this.min == 0) {
			return this.rng.nextInt(this.max) + 1;
		}
		return Mth.randomInt(this.rng, this.min, this.max);
	}
	
}
