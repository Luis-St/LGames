package net.vgc.game.action;

import net.luis.utils.util.ToString;
import net.vgc.game.action.data.GameActionData;

/**
 *
 * @author Luis-st
 *
 */

public record SimpleAction<T extends GameActionData> (int id, GameActionHandleType handleType, T data) implements GameAction<T> {
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
}
