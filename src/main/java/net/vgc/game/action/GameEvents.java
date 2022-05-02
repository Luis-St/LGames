package net.vgc.game.action;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;

import net.vgc.network.FriendlyByteBuffer;
import net.vgc.network.packet.InvalidPacketException;
import net.vgc.util.ReflectionHelper;
import net.vgc.util.Util;

public class GameEvents {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	protected static final Map<Integer, Class<? extends GameEvent>> GAME_EVENTS = Util.make(Maps.newHashMap(), (map) -> {
		
	});
	
	@Nullable
	public static Class<? extends GameEvent> byId(int id) {
		Class<? extends GameEvent> clazz = GAME_EVENTS.get(id);
		if (clazz != null) {
			return clazz;
		}
		LOGGER.warn("Unable to get game event for id {}", id);
		return null;
	}
	
	public static int getId(Class<? extends GameEvent> clazz) {
		for (Entry<Integer, Class<? extends GameEvent>> entry : GAME_EVENTS.entrySet()) {
			if (entry.getValue() == clazz) {
				return entry.getKey();
			}
		}
		LOGGER.warn("Unable to get game event id for game event {}", clazz.getSimpleName());
		return -1;
	}
	
	@Nullable
	public static GameEvent getEvent(int id, FriendlyByteBuffer buffer) {
		Class<? extends GameEvent> clazz = byId(id);
		if (clazz != null) {
			try {
				if (ReflectionHelper.hasConstructor(clazz, FriendlyByteBuffer.class)) {
					Constructor<? extends GameEvent> constructor = ReflectionHelper.getConstructor(clazz, FriendlyByteBuffer.class);
					return constructor.newInstance(buffer);
				} else {
					LOGGER.error("Game event {} does not have a constructor with FriendlyByteBuffer as parameter", clazz.getSimpleName());
					throw new InvalidPacketException("Game event " + clazz.getSimpleName() + " does not have a FriendlyByteBuffer constructor");
				}
			} catch (Exception e) {
				LOGGER.error("Fail to get create game event of type {} for id {}", clazz.getSimpleName(), id);
				throw new RuntimeException(e);
			}
		}
		return null;
	}
	
}
