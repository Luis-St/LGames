package net.luis.game;

/**
 *
 * @author Luis-st
 *
 */

public enum GameResult {
	
	WIN("win"), LOSE("lose"), DRAW("draw"), NO("no");
	
	private final String name;
	
	GameResult(String name) {
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
