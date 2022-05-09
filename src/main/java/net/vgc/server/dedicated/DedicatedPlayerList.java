package net.vgc.server.dedicated;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import net.vgc.network.Connection;
import net.vgc.network.packet.Packet;
import net.vgc.network.packet.client.PlayerAddPacket;
import net.vgc.network.packet.client.PlayerRemovePacket;
import net.vgc.network.packet.client.ServerClosedPacket;
import net.vgc.network.packet.client.SyncPermissionPacket;
import net.vgc.player.GameProfile;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Tickable;
import net.vgc.util.Util;

public class DedicatedPlayerList implements Tickable {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected final DedicatedServer server;
	protected final List<ServerPlayer> players;
	
	public DedicatedPlayerList(DedicatedServer server) {
		this.server = server;
		this.players = Lists.newArrayList();
	}
	
	@Override
	public void tick() {
		this.players.forEach(ServerPlayer::tick);
	}
	
	public void addPlayer(Connection connection, ServerPlayer player) {
		if (this.getPlayer(player.getProfile().getUUID()) == null) {
			player.connection = connection;
			this.players.add(player);
			this.broadcastAllExclude(new PlayerAddPacket(player.getProfile()), player);
			if (this.server.isAdmin(player)) {
				Util.runDelayed("PacketSendDelay", 250, () -> {
					this.broadcastAll(new SyncPermissionPacket(player.getProfile()));
				});
			} else if (this.server.getAdminPlayer() != null) {
				Util.runDelayed("DelayedPacketSender", 250, () -> {
					player.connection.send(new SyncPermissionPacket(this.server.getAdminPlayer().getProfile()));
				});
			}
			this.server.refreshPlayers();
			LOGGER.info("Add player {}", player.getProfile().getName());
		} else {
			LOGGER.warn("Fail to add player {}, since the player already exists in the player list", player.getProfile().getName());
		}
	}
	
	public void removePlayer(ServerPlayer player) {
		this.players.remove(player);
		this.broadcastAll(new PlayerRemovePacket(player.getProfile()));
		if (this.server.isAdmin(player)) {
			this.broadcastAll(new SyncPermissionPacket(GameProfile.EMPTY));
			LOGGER.info("Server admin left the server");
		}
		this.server.refreshPlayers();
		LOGGER.info("Remove player {}", player.getProfile().getName());
	}
	
	public void removeAllPlayers() {
		this.broadcastAll(new ServerClosedPacket());
		this.players.removeIf((player) -> {
			LOGGER.info("Remove player {}", player.getProfile().getName());
			if (Objects.equals(this.server.getAdmin(), player.getProfile().getUUID())) {
				LOGGER.info("Server admin left the server");
			}
			return true;
		});
		this.server.refreshPlayers();
	}
	
	public int playerCount() {
		return this.players.size();
	}
	
	@Nullable
	public ServerPlayer getPlayer(UUID uuid) {
		for (ServerPlayer player : this.players) {
			if (player.getProfile().getUUID().equals(uuid)) {
				return player;
			}
		}
		return null;
	}
	
	@Nullable
	public ServerPlayer getPlayer(GameProfile gameProfile) {
		return this.getPlayer(gameProfile.getUUID());
	}
	
	public List<ServerPlayer> getPlayers() {
		return this.players;
	}
	
	public List<ServerPlayer> getPlayers(List<GameProfile> profiles) {
		List<ServerPlayer> players = Lists.newArrayList();
		for (GameProfile profile : profiles) {
			ServerPlayer player = this.getPlayer(profile);
			if (player != null) {
				players.add(player);
			} else {
				LOGGER.warn("Fail to get player for profile {}", profile);
			}
		}
		return players;
	}
	
	public List<GameProfile> getProfiles() {
		return this.players.stream().map(ServerPlayer::getProfile).collect(Collectors.toList());
	}
	

	
	protected void broadcast(Packet<?> packet, ServerPlayer player) {
		if (player.connection.isConnected()) {
			player.connection.send(packet);
		} else if (this.players.contains(player)) {
			LOGGER.info("Fail to send packet of type {} to player {}, since the connection is closed", packet.getClass().getSimpleName(), player.getProfile().getName());
		} else {
			LOGGER.debug("Cancel sending packet of type {} to player {}, since the player leaves the server a few seconds ago", packet.getClass().getSimpleName(), player.getProfile().getName());
		}
	}
	
	public void broadcastAll(Packet<?> packet) {
		for (int i = 0; i < this.players.size(); i++) {
			this.broadcast(packet, this.players.get(i));
		}
	}
	
	public void broadcastAll(List<ServerPlayer> players, Packet<?> packet) {
		for (ServerPlayer player : players) {
			this.broadcast(packet, player);
		}
	}
	
	public void broadcastAllExclude(Packet<?> packet, ServerPlayer... players) {
		for (int i = 0; i < this.players.size(); i++) {
			ServerPlayer player = this.players.get(i);
			if (!Lists.newArrayList(players).contains(player)) {
				this.broadcast(packet, player);
			}
		}
	}
	
}
