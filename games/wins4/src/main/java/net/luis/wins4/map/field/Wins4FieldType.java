package net.luis.wins4.map.field;

import net.luis.game.map.field.GameFieldType;

/**
 *
 * @author Luis-st
 *
 */

public enum Wins4FieldType implements GameFieldType {
	
	DEFAULT("default");
	
	private final String name;
	
	Wins4FieldType(String name) {
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
