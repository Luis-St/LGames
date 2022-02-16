package net.project.util;

public class Mth {
	
	public static byte sum(byte b) {
		String s = String.valueOf(b);
		byte sum = 0;
		for (int j = 0; j < s.length(); j++) {
			sum += Integer.valueOf("" + s.charAt(j));
		}
		return sum;
	}
	
	public static int sum(int i) {
		String s = String.valueOf(i);
		int sum = 0;
		for (int j = 0; j < s.length(); j++) {
			sum += Integer.valueOf("" + s.charAt(j));
		}
		return sum;
	}
	
	public static long sum(long l) {
		String s = String.valueOf(l);
		long sum = 0;
		for (int j = 0; j < s.length(); j++) {
			sum += Integer.valueOf("" + s.charAt(j));
		}
		return sum;
	}
	
}
