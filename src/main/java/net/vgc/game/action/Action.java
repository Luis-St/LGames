package net.vgc.game.action;

import java.util.Objects;

import net.vgc.game.action.data.ActionData;
import net.vgc.game.action.type.ActionType;

public interface Action<T extends ActionData> {
	
	int id();
	
	default ActionType<?, ?> type() {
		return Objects.requireNonNull(ActionRegistry.getType(this.id()));
	}
	
	T data();
	
}
