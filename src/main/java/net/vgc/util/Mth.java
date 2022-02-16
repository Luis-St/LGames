package net.vgc.util;

import org.apache.logging.log4j.Logger;

public class Mth {
	
	protected static final Logger LOGGER = Util.getLogger(Mth.class);
	
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
	
}
