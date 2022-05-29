package net.vgc.game.games.ludo.map.field;

import net.vgc.game.map.field.GameFieldType;

public enum LudoFieldType implements GameFieldType {
	
	DEFAULT("default", 0),
	HOME("home", 1),
	WIN("win", 2);
	
	private final String name;
	private final int id;
	
	private LudoFieldType(String name, int id) {
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
	public Enum<LudoFieldType> getDefault() {
		return DEFAULT;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
