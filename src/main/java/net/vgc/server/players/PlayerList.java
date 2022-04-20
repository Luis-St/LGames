package net.vgc.server.players;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import net.vgc.data.serialization.SerializationUtil;
import net.vgc.data.tag.Tag;
import net.vgc.data.tag.tags.CompoundTag;
import net.vgc.data.tag.tags.collection.ListTag;
import net.vgc.network.Connection;
import net.vgc.network.packet.Packet;
import net.vgc.player.GameProfile;
import net.vgc.server.AbstractServer;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Tickable;

public abstract class PlayerList implements Tickable {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected final AbstractServer server;
	protected final Path playerDirectory;
	protected final List<ServerPlayer> players;
	protected final List<Pair<UUID, CompoundTag>> loadPlayers;
	
	public PlayerList(AbstractServer server, Path playerDirectory) {
		this.server = server;
		this.playerDirectory = playerDirectory;
		this.players = Lists.newArrayList();
		this.loadPlayers = Lists.newArrayList();
	}
	
	public void load() {
		try {
			if (!Files.exists(this.playerDirectory)) {
				LOGGER.info("No Players present, since file does not exists");	
			} else {
				Tag loadTag = Tag.load(this.playerDirectory);
				if (loadTag instanceof CompoundTag tag) {
					if (tag.contains("players", Tag.LIST_TAG)) {
						ListTag listTag = tag.getList("players", Tag.COMPOUND_TAG);
						if (listTag.isEmpty()) {
							LOGGER.info("No players present");
						} else {
							for (int i = 0; i < listTag.size(); i++) {
								CompoundTag playerTag = listTag.getCompoundTag(i);
								GameProfile gameProfile = SerializationUtil.deserialize(GameProfile.class, playerTag.getCompound("game_profile"));
								if (gameProfile != null) {
									LOGGER.debug("Load {} Player GameProfile", gameProfile);
									playerTag.remove("game_profile");
									this.loadPlayers.add(Pair.of(gameProfile.getUUID(), playerTag));
								} else {
									LOGGER.error("Fail to load Player");
									throw new NullPointerException("Something went wrong while loading players, since \"gameProfile\" of a Player is null");
								}
							}
						}
					} else {
						LOGGER.warn("Fail to load players from file {}, since the CompoundTag {} does not contains the key \"players\"", this.playerDirectory, loadTag);
					}
				} else {
					LOGGER.warn("Fail to load players from file {}, since Tag {} is not an instance of CompoundTag, but it is a type of {}", this.playerDirectory, loadTag, loadTag.getClass().getSimpleName());
				}
			}
			LOGGER.debug("Load {} players", this.loadPlayers.size());
		} catch (IOException e) {
			LOGGER.error("Fail to load players from file {}", this.playerDirectory);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void tick() {
		this.players.forEach(ServerPlayer::tick);
	}
	
	public void addPlayer(Connection connection, ServerPlayer player) {
		player.connection = connection;
		this.loadPlayer(player);
		this.players.add(player);
		LOGGER.info("Add Player {}", player.getGameProfile().getName());
	}
	
	protected void loadPlayer(ServerPlayer player) {
		player.load(this.getPlayerData(player.getGameProfile().getUUID()).getSecond());
	}
	
	public void removePlayer(ServerPlayer player) {
		this.savePlayer(player);
		this.players.remove(player);
		LOGGER.info("Remove Player {}", player.getGameProfile().getName());
	}
	
	protected void removeAllPlayers() {
		for (ServerPlayer player : this.players) {
			this.savePlayer(player);
		}
		this.players.removeIf((player) -> true);
	}
	
	protected void savePlayer(ServerPlayer player) {
		this.loadPlayers.remove(this.getPlayerData(player.getGameProfile().getUUID()));
		this.loadPlayers.add(Pair.of(player.getGameProfile().getUUID(), player.save()));
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
	
	@Nullable
	protected Pair<UUID, CompoundTag> getPlayerData(UUID uuid) {
		for (Pair<UUID, CompoundTag> pair : this.loadPlayers) {
			if (pair.getFirst().equals(uuid)) {
				return pair;
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
	
	public void save() {
		try {
			if (!Files.exists(this.playerDirectory)) {
				Files.createDirectories(this.playerDirectory.getParent());
				Files.createFile(this.playerDirectory);
			}
			this.removeAllPlayers();
			CompoundTag tag = new CompoundTag();
			ListTag listTag = new ListTag();
			for (CompoundTag playerTag : this.loadPlayers.stream().map(Pair::getSecond).collect(Collectors.toList())) {
				listTag.add(playerTag);
			}
			tag.putList("players", listTag);
			Tag.save(this.playerDirectory, tag);
		} catch (IOException e) {
			LOGGER.error("Fail to save players to file {}", this.playerDirectory);
			throw new RuntimeException(e);
		}
	}
	
}
