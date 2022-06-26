package net.vgc.game.games.wins4.map.field;

import net.vgc.game.map.field.GameFieldType;

public enum Wins4FieldType implements GameFieldType {
	
	DEFAULT("default", 0);

	private final String name;
	private final int id;
	
	private Wins4FieldType(String name, int id) {
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
	public Enum<Wins4FieldType> getDefault() {
		return DEFAULT;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

}
