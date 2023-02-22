package net.luis.server;

import net.luis.utils.util.ClassPathInspector;

/**
 *
 * @author Luis-st
 *
 */

public class Main {
	
	
	
	
	public static void main(String[] args) {
		for (Class<?> clazz : ClassPathInspector.getClasses()) {
			System.out.println(clazz.getName());
		}
	}
	
}
