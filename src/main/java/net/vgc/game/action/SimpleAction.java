package net.vgc.game.action;

import net.luis.utils.util.ToString;
import net.vgc.game.action.data.ActionData;

public record SimpleAction<T extends ActionData> (int id, T data) implements Action<T> {
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
}
