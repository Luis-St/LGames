package net.vgc.game.action;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;

import net.vgc.game.action.type.GameActionType;

public class ActionRegistry {
	
	private static final Map<Integer, GameActionType<?, ?>> ACTION_TYPES = Maps.newHashMap();
	
	public static void register(GameActionType<?, ?> type) {
		if (ACTION_TYPES.containsKey(type.getId())) {
			throw new IllegalArgumentException("An action type with ID " + type.getId() + " is already registered");
		} else {
			ACTION_TYPES.put(type.getId(), type);
		}
	}
	
	@Nullable
	public static GameActionType<?, ?> getType(int id) {
		return ACTION_TYPES.get(id);
	}
	
}
