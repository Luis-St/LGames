package net.vgc.game.action;

import net.vgc.game.action.data.ActionData;

public interface Action<T extends ActionData> {
	
	int id();
	
	T data();
	
}
