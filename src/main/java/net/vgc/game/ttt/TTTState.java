package net.vgc.game.ttt;

import net.vgc.util.EnumRepresentable;

public enum TTTState implements EnumRepresentable {
	
	DEFAULT("default", 0, ""),
	SHADOW("shadow", 1, "shadow"),
	WIN("win", 2, "win"),
	LOSE("lose", 3, "lose"),
	DRAW("draw", 4, "draw");
	
	private final String name;
	private final int id;
	private final String state;
	
	private TTTState(String name, int id, String state) {
		this.name = name;
		this.id = id;
		this.state = state;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public int getId() {
		return this.id;
	}
	
	public String getState() {
		return this.state;
	}
	
	@Override
	public Enum<TTTState> getDefault() {
		return DEFAULT;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
