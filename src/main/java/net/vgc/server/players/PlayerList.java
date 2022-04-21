package net.vgc.server.players;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import net.vgc.network.Connection;
import net.vgc.network.packet.Packet;
import net.vgc.server.AbstractServer;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Tickable;

public abstract class PlayerList implements Tickable {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected final AbstractServer server;
	protected final List<ServerPlayer> players;
	
	public PlayerList(AbstractServer server) {
		this.server = server;
		this.players = Lists.newArrayList();
	}
	
	@Override
	public void tick() {
		this.players.forEach(ServerPlayer::tick);
	}
	
	public void addPlayer(Connection connection, ServerPlayer player) {
		player.connection = connection;
		this.players.add(player);
		LOGGER.info("Add player {}", player.getGameProfile().getName());
	}
	
	public void removePlayer(ServerPlayer player) {
		this.players.remove(player);
		LOGGER.info("Remove player {}", player.getGameProfile().getName());
	}
	
	public void removeAllPlayers() {
		this.players.removeIf((player) -> {
			LOGGER.info("Remove player {}", player.getGameProfile().getName());
			if (this.server.getAdmin().equals(player.getGameProfile().getUUID())) {
				LOGGER.info("Server admin left the server");
			}
			return true;
		});
	}
	
	public int playerCount() {
		return this.players.size();
	}
	
	public List<ServerPlayer> getPlayers() {
		return this.players;
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
	
	public void broadcast(Packet<?> packet, ServerPlayer player) {
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
