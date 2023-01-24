package net.luis.ludo.map.field;

import net.luis.game.map.field.GameFieldType;

/**
 *
 * @author Luis-st
 *
 */

public enum LudoFieldType implements GameFieldType {
	
	DEFAULT("default"), HOME("home"), WIN("win");
	
	private final String name;
	
	LudoFieldType(String name) {
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
