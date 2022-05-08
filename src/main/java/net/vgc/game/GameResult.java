package net.vgc.game;

import javax.annotation.Nullable;

public enum GameResult {
	
	WIN(0, "win"),
	LOSE(1, "lose"),
	DRAW(2, "draw"),
	NO(3, "no");
	
	private final int id;
	private final String name;
	
	private GameResult(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	@Nullable
	public static GameResult fromId(int id) {
		for (GameResult result : values()) {
			if (result.getId() == id) {
				return result;
			}
		}
		return null;
	}
	
	@Nullable
	public static GameResult fromName(String name) {
		for (GameResult result : values()) {
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}
	
}
