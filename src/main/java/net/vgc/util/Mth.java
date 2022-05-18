package net.vgc.util;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Mth {
	
	protected static final Logger LOGGER = LogManager.getLogger(Mth.class);
	
	public static byte sum(byte b) {
		String s = String.valueOf(b);
		byte sum = 0;
		for (int j = 0; j < s.length(); j++) {
			try {
				sum += Byte.valueOf("" + s.charAt(j));
			} catch (NumberFormatException e) {
				LOGGER.warn("Fail to get byte for char {}", s.charAt(j));
			}
		}
		return sum;
	}
	
	public static int sum(int i) {
		String s = String.valueOf(i);
		int sum = 0;
		for (int j = 0; j < s.length(); j++) {
			try {
				sum += Integer.valueOf("" + s.charAt(j));
			} catch (NumberFormatException e) {
				LOGGER.warn("Fail to get int for char {}", s.charAt(j));
			}
		}
		return sum;
	}
	
	public static long sum(long l) {
		String s = String.valueOf(l);
		long sum = 0;
		for (int j = 0; j < s.length(); j++) {
			try {
				sum += Long.valueOf("" + s.charAt(j));
			} catch (NumberFormatException e) {
				LOGGER.warn("Fail to get long for char {}", s.charAt(j));
			}
		}
		return sum;
	}
	
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
	
	public static boolean sameValues(Number... numbers) {
		if (numbers.length == 0) {
			return false;
		} else if (numbers.length == 1) {
			return true;
		}
		Number number = numbers[0];
		for (int i = 1; i < numbers.length; i++) {
			if (!number.equals(numbers[i])) {
				return false;
			}
		}
		return true;
	}
	
}
