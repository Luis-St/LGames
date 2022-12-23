package net.vgc.games.ttt.map.field;

import net.vgc.game.map.field.GameFieldType;

/**
 *
 * @author Luis-st
 *
 */

public enum TTTFieldType implements GameFieldType {
	
	DEFAULT("default", 0);
	
	private final String name;
	private final int id;
	
	private TTTFieldType(String name, int id) {
		this.name = name;
		this.id = id;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public int getId() {
		return this.id;
	}
	
	@Override
	public Enum<TTTFieldType> getDefault() {
		return DEFAULT;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
