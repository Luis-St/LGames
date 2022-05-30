package net.vgc.client.game.games.ludo.map.field;

import net.vgc.client.game.map.field.FieldRenderState;

public enum LudoFieldRenderState implements FieldRenderState {
	
	DEFAULT("default", 0),
	NO("no", 2);
	
	private final String name;
	private final int id;
	
	private LudoFieldRenderState(String name, int id) {
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
	public Enum<LudoFieldRenderState> getDefault() {
		return NO;
	}
	
	@Override
	public boolean canRenderWithFigure() {
		return this == DEFAULT;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
