package net.vgc.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import javafx.stage.Stage;
import net.vgc.data.tag.Tag;
import net.vgc.data.tag.tags.CompoundTag;
import net.vgc.network.Connection;
import net.vgc.player.GameProfile;
import net.vgc.server.dedicated.DedicatedPlayerList;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Tickable;

public abstract class AbstractServer implements Tickable {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	protected static final boolean NATIVE = Epoll.isAvailable();
	
	protected final EventLoopGroup group = NATIVE ? new EpollEventLoopGroup() : new NioEventLoopGroup();
	protected final String host;
	protected final int port;
	protected final Path serverDirectory;
	protected DedicatedPlayerList playerList;
	protected UUID admin;
	protected ServerPlayer adminPlayer;
	
	protected AbstractServer(String host, int port, Path serverDirectory) {
		this.host = host;
		this.port = port;
		this.serverDirectory = serverDirectory;
	}
	
	public abstract boolean isDedicated();
	
	public void init() throws IOException {
		Path path = this.serverDirectory.resolve("server.data");
		if (Files.exists(path)) {
			Tag loadTag = Tag.load(path);
			if (loadTag instanceof CompoundTag tag) {
				this.load(tag);
			} else {
				LOGGER.warn("Fail to load server from {}, since Tag {} is not a instance of CompoundTag but it is a Tag of type {}", path, loadTag, loadTag.getClass().getSimpleName());
			}
		}
	}
	
	protected abstract void load(CompoundTag tag);
	
	public abstract void displayServer(Stage stage);
	
	public abstract void startServer();
	
	public void enterPlayer(Connection connection, GameProfile gameProfile) {
		ServerPlayer player = new ServerPlayer(gameProfile);
		this.playerList.addPlayer(connection, player);
		if (this.admin != null && this.admin.equals(gameProfile.getUUID())) {
			if (this.adminPlayer == null) {
				LOGGER.info("Server admin joined the server");
				this.adminPlayer = player;
			} else {
				LOGGER.error("Unable to set admin Player to {}, since he is already set {}", player, this.adminPlayer);
				throw new IllegalStateException("Multiple server admins are not allowed");
			}
		}
	}
	
	public void leavePlayer(Connection connection, ServerPlayer player) {
		this.playerList.removePlayer(player);
		if (this.admin != null && this.admin.equals(player.getGameProfile().getUUID()) && this.adminPlayer != null) {
			LOGGER.info("Server admin left the server");
			this.adminPlayer = null;
		}
	}
	
	@Override
	public void tick() {
		this.playerList.tick();
	}
	
	public String getHost() {
		return this.host;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public UUID getAdmin() {
		return this.admin;
	}
	
	public void setAdmin(UUID admin) {
		this.admin = admin;
	}
	
	public boolean isAdmin(ServerPlayer player) {
		return player.getGameProfile().getUUID().equals(this.admin);
	}
	
	@Nullable
	public ServerPlayer getAdminPlayer() {
		if (this.playerList != null && this.adminPlayer == null) {
			this.adminPlayer = this.playerList.getPlayer(this.admin);
		}
		return this.adminPlayer;
	}
	
	public DedicatedPlayerList getPlayerList() {
		return this.playerList;
	}
	
	public void stopServer() throws Exception {
		this.adminPlayer = null;
		this.playerList.removeAllPlayers();
		Tag.save(this.serverDirectory.resolve("server.data"), this.save());
		LOGGER.debug("Save server data");
	}
	
	protected abstract CompoundTag save();
	
}
