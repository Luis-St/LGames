package net.vgc.game;

import net.vgc.util.EnumRepresentable;

public enum GameResult implements EnumRepresentable {
	
	WIN("win", 0), LOSE("lose", 1), DRAW("draw", 2), NO("no", 3);
	
	private final String name;
	private final int id;
	
	private GameResult(String name, int id) {
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
	public Enum<GameResult> getDefault() {
		return NO;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
