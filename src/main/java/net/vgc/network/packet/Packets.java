package net.vgc.network.packet;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;

import net.vgc.network.FriendlyByteBuffer;
import net.vgc.network.packet.account.ClientExitPacket;
import net.vgc.network.packet.account.ClientLoginPacket;
import net.vgc.network.packet.account.ClientLogoutPacket;
import net.vgc.network.packet.client.ClientJoinedPacket;
import net.vgc.network.packet.client.ClientLoggedInPacket;
import net.vgc.network.packet.client.ClientLoggedOutPacket;
import net.vgc.network.packet.client.PlayerAddPacket;
import net.vgc.network.packet.client.PlayerRemovePacket;
import net.vgc.network.packet.client.ServerClosedPacket;
import net.vgc.network.packet.client.SyncPermissionPacket;
import net.vgc.network.packet.client.game.CancelPlayGameRequestPacket;
import net.vgc.network.packet.client.game.ExitGamePacket;
import net.vgc.network.packet.client.game.StartTTTGamePacket;
import net.vgc.network.packet.client.game.StopGamePacket;
import net.vgc.network.packet.client.game.TTTGameResultPacket;
import net.vgc.network.packet.client.game.UpdateTTTGamePacket;
import net.vgc.network.packet.server.ClientJoinPacket;
import net.vgc.network.packet.server.ClientLeavePacket;
import net.vgc.network.packet.server.ExitGameRequestPacket;
import net.vgc.network.packet.server.PlayGameRequestPacket;
import net.vgc.network.packet.server.game.PressTTTFieldPacket;
import net.vgc.util.ReflectionHelper;
import net.vgc.util.Util;


public class Packets {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	protected static final Map<Integer, Class<? extends Packet<?>>> PACKETS = Util.make(Maps.newHashMap(), (map) -> {
		int i = 0;
		map.put(i++, ClientLoginPacket.class);
		map.put(i++, ClientLogoutPacket.class);
		map.put(i++, ClientLoggedInPacket.class);
		map.put(i++, ClientLoggedOutPacket.class);
		map.put(i++, ClientExitPacket.class);
		map.put(i++, ClientJoinPacket.class);
		map.put(i++, ClientLeavePacket.class);
		map.put(i++, ClientJoinedPacket.class);
		map.put(i++, SyncPermissionPacket.class);
		map.put(i++, PlayerAddPacket.class);
		map.put(i++, PlayerRemovePacket.class);
		map.put(i++, PlayGameRequestPacket.class);
		map.put(i++, CancelPlayGameRequestPacket.class);
		map.put(i++, StartTTTGamePacket.class);
		map.put(i++, ExitGameRequestPacket.class);
		map.put(i++, ExitGamePacket.class);
		map.put(i++, StopGamePacket.class);
		map.put(i++, PressTTTFieldPacket.class);
		map.put(i++, UpdateTTTGamePacket.class);
		map.put(i++, TTTGameResultPacket.class);
		map.put(i++, ServerClosedPacket.class);
	});
	
	@Nullable
	public static Class<? extends Packet<?>> byId(int id) {
		Class<? extends Packet<?>> clazz = PACKETS.get(id);
		if (clazz != null) {
			return clazz;
		}
		LOGGER.warn("Unable to get packet for id {}", id);
		return null;
	}
	
	public static int getId(Class<? extends Packet<?>> clazz) {
		for (Entry<Integer, Class<? extends Packet<?>>> entry : PACKETS.entrySet()) {
			if (entry.getValue() == clazz) {
				return entry.getKey();
			}
		}
		LOGGER.warn("Unable to get packet id for packet {}", clazz.getSimpleName());
		return -1;
	}
	
	@Nullable
	public static Packet<?> getPacket(int id, FriendlyByteBuffer buffer) {
		Class<? extends Packet<?>> clazz = byId(id);
		if (clazz != null) {
			try {
				if (ReflectionHelper.hasConstructor(clazz, FriendlyByteBuffer.class)) {
					Constructor<? extends Packet<?>> constructor = ReflectionHelper.getConstructor(clazz, FriendlyByteBuffer.class);
					return constructor.newInstance(buffer);
				} else {
					LOGGER.error("Packet {} does not have a constructor with FriendlyByteBuffer as parameter", clazz.getSimpleName());
					throw new InvalidPacketException("Packet " + clazz.getSimpleName() + " does not have a FriendlyByteBuffer constructor");
				}
			} catch (Exception e) {
				LOGGER.error("Fail to get create packet of type {} for id {}", clazz.getSimpleName(), id);
				throw new RuntimeException(e);
			}
		}
		return null;
	}
	
}
