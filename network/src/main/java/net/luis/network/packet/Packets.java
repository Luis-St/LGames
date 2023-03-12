package net.luis.network.packet;

import com.google.common.collect.Maps;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.exception.InvalidPacketException;
import net.luis.network.packet.account.ClientExitPacket;
import net.luis.network.packet.account.ClientLoginPacket;
import net.luis.network.packet.account.ClientLogoutPacket;
import net.luis.network.packet.account.ClientRegistrationPacket;
import net.luis.network.packet.client.*;
import net.luis.network.packet.client.game.*;
import net.luis.network.packet.client.game.dice.CanRollDiceAgainPacket;
import net.luis.network.packet.client.game.dice.CancelRollDiceRequestPacket;
import net.luis.network.packet.client.game.dice.RolledDicePacket;
import net.luis.network.packet.server.ClientJoinPacket;
import net.luis.network.packet.server.ClientLeavePacket;
import net.luis.network.packet.server.PlayGameRequestPacket;
import net.luis.network.packet.server.game.ExitGameRequestPacket;
import net.luis.network.packet.server.game.PlayAgainGameRequestPacket;
import net.luis.network.packet.server.game.SelectGameFieldPacket;
import net.luis.network.packet.server.game.dice.RollDiceRequestPacket;
import net.luis.utils.util.Utils;
import net.luis.utils.util.reflection.ReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
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
	
	private static final Logger LOGGER = LogManager.getLogger(Packets.class);
	private static final Map<Integer, Class<? extends Packet>> PACKETS = Utils.make(Maps.newHashMap(), (map) -> {
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
		map.put(i, ServerClosedPacket.class);
	});
	
	public static @Nullable Class<? extends Packet> byId(int id) {
		Class<? extends Packet> clazz = PACKETS.get(id);
		if (clazz != null) {
			return clazz;
		}
		LOGGER.error("Failed to get packet for id {}", id);
		return null;
	}
	
	public static int getId(@NotNull Class<? extends Packet> clazz) {
		for (Entry<Integer, Class<? extends Packet>> entry : PACKETS.entrySet()) {
			if (entry.getValue() == clazz) {
				return entry.getKey();
			}
		}
		LOGGER.error("Failed to get packet id for packet of type {}", clazz.getSimpleName());
		return -1;
	}
	
	public static @Nullable Packet getPacket(int id, @NotNull FriendlyByteBuffer buffer) {
		Class<? extends Packet> clazz = byId(id);
		if (clazz != null) {
			try {
				if (ReflectionHelper.hasConstructor(clazz, FriendlyByteBuffer.class)) {
					Constructor<? extends Packet> constructor = ReflectionHelper.getConstructor(clazz, FriendlyByteBuffer.class);
					assert constructor != null;
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
