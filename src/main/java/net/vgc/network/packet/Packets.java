package net.vgc.network.packet;

import com.google.common.collect.Maps;
import net.luis.utils.util.ReflectionHelper;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.account.ClientExitPacket;
import net.vgc.network.packet.account.ClientLoginPacket;
import net.vgc.network.packet.account.ClientLogoutPacket;
import net.vgc.network.packet.account.ClientRegistrationPacket;
import net.vgc.network.packet.client.*;
import net.vgc.network.packet.client.game.*;
import net.vgc.network.packet.client.game.dice.CanRollDiceAgainPacket;
import net.vgc.network.packet.client.game.dice.CancelRollDiceRequestPacket;
import net.vgc.network.packet.client.game.dice.RolledDicePacket;
import net.vgc.network.packet.server.ClientJoinPacket;
import net.vgc.network.packet.server.ClientLeavePacket;
import net.vgc.network.packet.server.PlayGameRequestPacket;
import net.vgc.network.packet.server.game.ExitGameRequestPacket;
import net.vgc.network.packet.server.game.PlayAgainGameRequestPacket;
import net.vgc.network.packet.server.game.SelectGameFieldPacket;
import net.vgc.network.packet.server.game.dice.RollDiceRequestPacket;
import net.vgc.util.Util;
import net.vgc.util.exception.InvalidPacketException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Luis-st
 *
 */

public class Packets {
	
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<Integer, Class<? extends Packet>> PACKETS = Util.make(Maps.newHashMap(), (map) -> {
		int i = 0;
		map.put(i++, ClientRegistrationPacket.class);
		map.put(i++, ClientLoginPacket.class);
		map.put(i++, ClientLogoutPacket.class);
		map.put(i++, ClientLoggedInPacket.class);
		map.put(i++, ClientLoggedOutPacket.class);
		map.put(i++, ClientExitPacket.class);
		map.put(i++, ClientJoinPacket.class);
		map.put(i++, ClientLeavePacket.class);
		map.put(i++, ClientJoinedPacket.class);
		map.put(i++, PlayerAddPacket.class);
		map.put(i++, PlayerRemovePacket.class);
		map.put(i++, SyncPermissionPacket.class);
		map.put(i++, SyncPlayerDataPacket.class);
		map.put(i++, PlayGameRequestPacket.class);
		map.put(i++, CancelPlayGameRequestPacket.class);
		map.put(i++, StartGamePacket.class);
		map.put(i++, PlayAgainGameRequestPacket.class);
		map.put(i++, CancelPlayAgainGameRequestPacket.class);
		map.put(i++, CurrentPlayerUpdatePacket.class);
		map.put(i++, RollDiceRequestPacket.class);
		map.put(i++, CancelRollDiceRequestPacket.class);
		map.put(i++, RolledDicePacket.class);
		map.put(i++, CanRollDiceAgainPacket.class);
		map.put(i++, GameActionFailedPacket.class);
		map.put(i++, SelectGameFieldPacket.class);
		map.put(i++, CanSelectGameFieldPacket.class);
		map.put(i++, UpdateGameMapPacket.class);
		map.put(i++, GameResultPacket.class);
		map.put(i++, ExitGameRequestPacket.class);
		map.put(i++, ExitGamePacket.class);
		map.put(i++, StopGamePacket.class);
		map.put(i++, ServerClosedPacket.class);
	});
	
	@Nullable
	public static Class<? extends Packet> byId(int id) {
		Class<? extends Packet> clazz = PACKETS.get(id);
		if (clazz != null) {
			return clazz;
		}
		LOGGER.warn("Fail to get packet for id {}", id);
		return null;
	}
	
	public static int getId(Class<? extends Packet> clazz) {
		for (Entry<Integer, Class<? extends Packet>> entry : PACKETS.entrySet()) {
			if (entry.getValue() == clazz) {
				return entry.getKey();
			}
		}
		LOGGER.warn("Fail to get packet id for packet of type {}", clazz.getSimpleName());
		return -1;
	}
	
	@Nullable
	public static Packet getPacket(int id, FriendlyByteBuffer buffer) {
		Class<? extends Packet> clazz = byId(id);
		if (clazz != null) {
			try {
				if (ReflectionHelper.hasConstructor(clazz, FriendlyByteBuffer.class)) {
					Constructor<? extends Packet> constructor = ReflectionHelper.getConstructor(clazz, FriendlyByteBuffer.class);
					return constructor.newInstance(buffer);
				} else {
					LOGGER.error("Packet {} does not have a constructor with FriendlyByteBuffer as parameter", clazz.getSimpleName());
					throw new InvalidPacketException("Packet " + clazz.getSimpleName() + " does not have a FriendlyByteBuffer constructor");
				}
			} catch (Exception e) {
				LOGGER.error("Fail to create packet of type {} for id {}", clazz.getSimpleName(), id);
				throw new RuntimeException(e);
			}
		}
		return null;
	}
	
}
