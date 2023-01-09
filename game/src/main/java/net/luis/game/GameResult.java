package net.luis.game;

import net.luis.common.util.EnumRepresentable;

/**
 *
 * @author Luis-st
 *
 */

public enum GameResult implements EnumRepresentable {
	
	WIN("win", 0), LOSE("lose", 1), DRAW("draw", 2), NO("no", 3);
	
	private final String name;
	private final int id;
	
	GameResult(String name, int id) {
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
