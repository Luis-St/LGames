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
		if (this.getPlayer(player.getGameProfile().getUUID()) == null) {
			player.connection = connection;
			this.players.add(player);
			this.broadcastAllExclude(new PlayerAddPacket(player.getGameProfile()), player);
			if (this.server.isAdmin(player)) {
				Util.runDelayed("PacketSendDelay", 1000, () -> {
					player.connection.send(new SyncPermissionPacket(player.getGameProfile()));
				});
			} else if (this.server.getAdminPlayer() != null) {
				Util.runDelayed("PacketSendDelay", 1000, () -> {
					player.connection.send(new SyncPermissionPacket(this.server.getAdminPlayer().getGameProfile()));
				});
			}
			this.server.refreshPlayers();
			LOGGER.info("Add player {}", player.getGameProfile().getName());
		} else {
			LOGGER.warn("Fail to add player {}, since the player already exists in the player list", player.getGameProfile().getName());
		}
	}
	
	public void removePlayer(ServerPlayer player) {
		this.players.remove(player);
		this.broadcastAll(new PlayerRemovePacket(player.getGameProfile()));
		if (this.server.isAdmin(player)) {
			this.broadcastAll(new SyncPermissionPacket(GameProfile.EMPTY));
			LOGGER.info("Server admin left the server");
		}
		this.server.refreshPlayers();
		LOGGER.info("Remove player {}", player.getGameProfile().getName());
	}
	
	public void removeAllPlayers() {
		this.broadcastAll(new ServerClosedPacket());
		this.players.removeIf((player) -> {
			LOGGER.info("Remove player {}", player.getGameProfile().getName());
			if (Objects.equals(this.server.getAdmin(), player.getGameProfile().getUUID())) {
				LOGGER.info("Server admin left the server");
			}
			return true;
		});
		this.server.refreshPlayers();
	}
	
	public int playerCount() {
		return this.players.size();
	}
	
	public List<ServerPlayer> getPlayers() {
		return this.players;
	}
	
	public List<ServerPlayer> getPlayers(List<GameProfile> gameProfiles) {
		List<ServerPlayer> players = Lists.newArrayList();
		for (GameProfile gameProfile : gameProfiles) {
			ServerPlayer player = this.getPlayer(gameProfile.getUUID());
			if (player != null) {
				players.add(player);
			} else {
				LOGGER.warn("Fail to get player for game profile {}", gameProfile);
			}
		}
		return players;
	}
	
	public List<GameProfile> getGameProfiles() {
		return this.players.stream().map(ServerPlayer::getGameProfile).collect(Collectors.toList());
	}
	
	@Nullable
	public ServerPlayer getPlayer(UUID uuid) {
		for (ServerPlayer player : this.players) {
			if (player.getGameProfile().getUUID().equals(uuid)) {
				return player;
			}
		}
		return null;
	}
	
	protected void broadcast(Packet<?> packet, ServerPlayer player) {
		player.connection.send(packet);
	}
	
	public void broadcastAll(Packet<?> packet) {
		for (int i = 0; i < this.players.size(); i++) {
			this.broadcast(packet, this.players.get(i));
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
