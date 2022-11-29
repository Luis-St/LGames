package net.vgc.util;

import java.util.Random;

public class Mth {
	
	public static int randomInt(Random rng, int min, int max) {
		return min + rng.nextInt(max - min + 1);
	}
	
	public static double roundTo(double value, double roundValue) {
		double d = Math.round(value * roundValue);
		return d / roundValue;
	}
	
	public static boolean isInBounds(int value, int min, int max) {
		if (max >= value && value >= min) {
			return true;
		}
		return false;
	}
	
}
