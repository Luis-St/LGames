package net.vgc.game.action;

import net.vgc.game.Game;
import net.vgc.game.action.data.ActionData;

public interface Action<T extends ActionData> {
	
	ActionType<Action<T>, T> getType();
	
	T getData();
	
	void handle(Game game);
	
}
