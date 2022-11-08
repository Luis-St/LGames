package net.vgc.game.action;

import net.vgc.game.action.data.ActionData;

public interface ActionFactory<T extends Action<V>, V extends ActionData> {
	
	T create(V data);
	
}
