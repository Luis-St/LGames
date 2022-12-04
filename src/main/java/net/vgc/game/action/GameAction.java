package net.vgc.game.action;

import java.util.Objects;

import net.vgc.game.action.data.GameActionData;
import net.vgc.game.action.type.GameActionType;

public interface GameAction<T extends GameActionData> {
	
	int id();
	
	default GameActionType<?, ?> type() {
		return Objects.requireNonNull(ActionRegistry.getType(this.id()));
	}
	
	GameActionHandleType handleType();
	
	T data();
	
}
