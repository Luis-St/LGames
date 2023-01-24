package net.luis.ttt.map.field;

import net.luis.game.map.field.GameFieldType;

/**
 *
 * @author Luis-st
 *
 */

public enum TTTFieldType implements GameFieldType {
	
	DEFAULT("default");
	
	private final String name;
	
	TTTFieldType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
