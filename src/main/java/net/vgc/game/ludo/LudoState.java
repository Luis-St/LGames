package net.vgc.game.ludo;

import net.vgc.util.EnumRepresentable;

public enum LudoState implements EnumRepresentable {
	
	DEFAULT("default", 0),
	SHADOW("shadow", 1);
	
	private final String name;
	private final int id;
	
	private LudoState(String name, int id) {
		this.name = name;
		this.id = id;
	}
	
	@Override
	public String getName() {
		return this.name ;
	}

	@Override
	public int getId() {
		return this.id;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
