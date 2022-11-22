package net.vgc.game.action;

import net.vgc.game.action.data.ActionData;
import net.vgc.util.ToString;

public record SimpleAction<T extends ActionData> (int id, T data) implements Action<T> {
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
}
