package net.vgc.client.game.games.wins4.map.field;

import net.vgc.client.game.map.field.FieldRenderState;

public enum Wins4FieldRenderState implements FieldRenderState {
	
	DEFAULT("default", 0, ""),
	SHADOW("shadow", 1, "shadow"),
	NO("no", 5, null);
	
	private final String name;
	private final int id;
	private final String state;
	
	private Wins4FieldRenderState(String name, int id, String state) {
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
	public Enum<Wins4FieldRenderState> getDefault() {
		return DEFAULT;
	}
	
	@Override
	public boolean canRenderWithFigure() {
		if (this == SHADOW || this == NO) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

}
