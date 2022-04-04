package net.vgc.network.packet;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import net.vgc.network.FriendlyByteBuffer;
import net.vgc.util.ReflectionHelper;
import net.vgc.util.Util;


public class Packets {
	
	protected static final Map<Integer, Class<? extends Packet<?>>> PACKETS = Util.make(Maps.newHashMap(), (map) -> {
		
	});
	
	public static Class<? extends Packet<?>> byId(int id) {
		return PACKETS.get(id);
	}
	
	public static int getId(Class<? extends Packet<?>> clazz) {
		for (Entry<Integer, Class<? extends Packet<?>>> entry : PACKETS.entrySet()) {
			if (entry.getValue() == clazz) {
				return entry.getKey();
			}
		}
		return -1;
	}
	
	public static Packet<?> getPacket(int id, FriendlyByteBuffer buffer) {
		Class<? extends Packet<?>> clazz = byId(id);
		if (clazz != null) {
			try {
				if (ReflectionHelper.hasConstructor(clazz, FriendlyByteBuffer.class)) {
					Constructor<? extends Packet<?>> constructor = ReflectionHelper.getConstructor(clazz, FriendlyByteBuffer.class);
					return constructor.newInstance(buffer);
				}
			} catch (Exception e) {
				
			}
		}
		return null;
	}
	
}
