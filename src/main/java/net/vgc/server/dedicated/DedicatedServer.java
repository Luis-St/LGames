package net.vgc.server.dedicated;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.vgc.Constans;
import net.vgc.client.fx.FxUtil;
import net.vgc.data.tag.Tag;
import net.vgc.data.tag.TagUtil;
import net.vgc.data.tag.tags.CompoundTag;
import net.vgc.game.Game;
import net.vgc.language.TranslationKey;
import net.vgc.network.Connection;
import net.vgc.network.NetworkSide;
import net.vgc.network.PacketDecoder;
import net.vgc.network.PacketEncoder;
import net.vgc.player.GameProfile;
import net.vgc.server.network.ServerPacketListener;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.ExceptionHandler;
import net.vgc.util.Tickable;

public class DedicatedServer implements Tickable  {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	protected static final boolean NATIVE = Epoll.isAvailable();
	
	protected final EventLoopGroup group = NATIVE ? new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("connection #%d").setUncaughtExceptionHandler(new ExceptionHandler()).build())
		: new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("connection #%d").setUncaughtExceptionHandler(new ExceptionHandler()).build());
	protected final String host;
	protected final int port;
	protected final Path serverDirectory;
	protected final DedicatedPlayerList playerList;
	protected final List<Channel> channels = Lists.newArrayList();
	protected final List<Connection> connections = Lists.newArrayList();
	protected TreeItem<String> playersTreeItem;
	protected UUID admin;
	protected ServerPlayer adminPlayer;
	protected Game game;
	
	public DedicatedServer(String host, int port, Path serverDirectory) throws Exception {
		this.host = host;
		this.port = port;
		this.serverDirectory = serverDirectory;
		this.playerList = new DedicatedPlayerList(this);
		if (!Files.exists(serverDirectory)) {
			Files.createDirectories(serverDirectory.getParent());
			LOGGER.debug("Create server directory");
		}
	}
	
	public void init() throws IOException {
		Path path = this.serverDirectory.resolve("server.data");
		if (Files.exists(path)) {
			Tag loadTag = Tag.load(path);
			if (loadTag instanceof CompoundTag tag) {
				this.load(tag);
			} else {
				LOGGER.warn("Fail to load server from {}, since tag {} is not a instance of CompoundTag but it is a tag of type {}", path, loadTag, loadTag.getClass().getSimpleName());
			}
		}
	}
	
	protected void load(CompoundTag tag) {
		if (tag.contains("admin")) {
			this.admin = TagUtil.readUUID(tag.getCompound("admin"));
		}
	}
	
	public void displayServer(Stage stage) {
		stage.setScene(this.makeScene());
		stage.setTitle(TranslationKey.createAndGet("server.constans.name"));
		stage.setResizable(false);
		stage.show();
	}
	
	protected Scene makeScene() {
		VBox box = new VBox();
		TreeView<String> serverTree = new TreeView<>();
		TreeItem<String> treeItem = new TreeItem<String>(TranslationKey.createAndGet("server.window.server"));
		treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("server.window.server_host", this.host)));
		treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("server.window.server_port", this.port)));
		treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("server.window.server_admin", this.admin)));
		this.playersTreeItem = new TreeItem<String>(TranslationKey.createAndGet("server.window.players"));
		for (ServerPlayer player : this.playerList.getPlayers()) {
			this.playersTreeItem.getChildren().add(player.display());
		}
		treeItem.getChildren().add(this.playersTreeItem);
		serverTree.setRoot(treeItem);
		serverTree.setShowRoot(Constans.DEBUG);
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 5.0, 5.0);
		Button settingsButton = FxUtil.makeButton(TranslationKey.createAndGet("screen.menu.settings"), this::openSettings);
		settingsButton.setPrefWidth(150.0);
		Button refreshButton = FxUtil.makeButton(TranslationKey.createAndGet("account.window.refresh"), this::refreshPlayers);
		refreshButton.setPrefWidth(150.0);
		Button closeButton = FxUtil.makeButton(TranslationKey.createAndGet("account.window.close"), Platform::exit);
		closeButton.setPrefWidth(150.0);
		pane.addRow(0, settingsButton, refreshButton, closeButton);
		box.getChildren().addAll(serverTree, pane);
		return new Scene(box, 450.0, 400.0);
	}
	
	protected void openSettings() {
		LOGGER.debug("Settings");
	}
	
	public void refreshPlayers() {
		this.playersTreeItem.getChildren().removeIf((string) -> true);
		for (ServerPlayer player : this.playerList.getPlayers()) {
			this.playersTreeItem.getChildren().add(player.display());
		}
	}

	public void startServer() {
		new ServerBootstrap().group(this.group).channel(NATIVE ? EpollServerSocketChannel.class : NioServerSocketChannel.class).childHandler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				ChannelPipeline pipeline = channel.pipeline();
				Connection connection = new Connection(new ServerPacketListener(DedicatedServer.this, NetworkSide.SERVER));
				pipeline.addLast("decoder", new PacketDecoder());
				pipeline.addLast("encoder", new PacketEncoder());
				pipeline.addLast("handler", connection);
				DedicatedServer.this.channels.add(channel);
				DedicatedServer.this.connections.add(connection);
				LOGGER.debug("Client connected with address {}", channel.remoteAddress().toString().replace("/", ""));
			}
		}).localAddress(this.host, this.port).bind().syncUninterruptibly().channel();
		LOGGER.info("Launch dedicated virtual game collection server on host {} with port {}", this.host, this.port);
	}
	
	public void enterPlayer(Connection connection, GameProfile profile) {
		ServerPlayer player = new ServerPlayer(profile, this);
		this.playerList.addPlayer(connection, player);
		if (this.admin != null && this.admin.equals(profile.getUUID())) {
			if (this.adminPlayer == null) {
				LOGGER.info("Server admin joined the server");
				this.adminPlayer = player;
			} else {
				LOGGER.error("Unable to set admin player to {}, since he is already set {}", player, this.adminPlayer);
				throw new IllegalStateException("Multiple server admins are not allowed");
			}
		}
	}
	
	public void leavePlayer(Connection connection, ServerPlayer player) {
		if (this.channels.contains(connection.getChannel()) && this.connections.contains(connection)) {
			this.channels.remove(connection.getChannel());
			this.connections.remove(connection);
			LOGGER.debug("Remove channel and connection for player {}", player != null ? player.getProfile().getName() : "");
		}
		if (player != null) {
			this.playerList.removePlayer(player);
			if (this.admin != null && this.admin.equals(player.getProfile().getUUID()) && this.adminPlayer != null) {
				LOGGER.info("Server admin left the server");
				this.adminPlayer = null;
			}
			if (this.game != null) {
				if (this.game.getPlayers().contains(player)) {
					this.game.removePlayer(player, false);
				}
			}
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
		return player.getProfile().getUUID().equals(this.admin);
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
	
	public Game getGame() {
		return this.game;
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	public void stopServer() throws Exception {
		this.adminPlayer = null;
		this.playerList.removeAllPlayers();
		Tag.save(this.serverDirectory.resolve("server.data"), this.save());
		LOGGER.debug("Save server data");
		this.connections.clear();
		this.channels.forEach(Channel::close);
		this.channels.clear();
		this.group.shutdownGracefully();
	}
	
	protected CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		if (this.admin != null)  {
			tag.putCompound("admin", TagUtil.writeUUID(this.admin));
		}
		return tag;
	}

}
