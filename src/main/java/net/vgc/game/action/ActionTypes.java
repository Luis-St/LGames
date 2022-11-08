package net.vgc.game.action;

import java.util.Map;

import com.google.common.collect.Maps;

public class ActionTypes {
	
	private static final Map<Integer, ActionType<?, ?>> TYPES = Maps.newHashMap();
	
	
	
	@SuppressWarnings("unused")
	private static void register(ActionType<?, ?> type) {
		if (TYPES.containsKey(type.getId())) {
			throw new RuntimeException("Fail to register ActionType " + type.getName() + " to id " + type.getId() + ", since there is already a ActionType registered");
		} else {
			TYPES.put(type.getId(), type);
		}
	}
	
	public static ActionType<?, ?> getType(int id) {
		if (TYPES.containsKey(id)) {
			return TYPES.get(id);
		}
		throw new NullPointerException("Fail to get ActionType for id " + id);
	}
	
}
