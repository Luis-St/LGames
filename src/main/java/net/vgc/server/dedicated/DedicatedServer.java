package net.vgc.server.dedicated;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

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
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.luis.fxutils.FxUtils;
import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.tags.CompoundTag;
import net.vgc.Constans;
import net.vgc.game.Game;
import net.vgc.game.player.GamePlayer;
import net.vgc.game.score.PlayerScore;
import net.vgc.language.TranslationKey;
import net.vgc.network.Connection;
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.PacketDecoder;
import net.vgc.network.packet.PacketEncoder;
import net.vgc.network.packet.PacketListener;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.player.GameProfile;
import net.vgc.server.network.ServerPacketHandler;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.ExceptionHandler;
import net.vgc.util.Tickable;

public class DedicatedServer implements Tickable, PacketListener<ServerPacket> {
	
	private static final Logger LOGGER = LogManager.getLogger();
	private static final boolean NATIVE = Epoll.isAvailable();
	
	private final EventLoopGroup group = NATIVE ? new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("connection #%d").setUncaughtExceptionHandler(new ExceptionHandler()).build())
		: new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("connection #%d").setUncaughtExceptionHandler(new ExceptionHandler()).build());
	private final String host;
	private final int port;
	private final Path serverDirectory;
	private final DedicatedPlayerList playerList;
	private final List<Channel> channels = Lists.newArrayList();
	private final List<Connection> connections = Lists.newArrayList();
	private TreeItem<String> playersTreeItem;
	private UUID admin;
	private ServerPlayer adminPlayer;
	private Game game;
	
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
	
	private void load(CompoundTag tag) {
		
	}
	
	public void displayServer(Stage stage) {
		stage.setScene(this.makeScene());
		stage.setTitle(TranslationKey.createAndGet("server.constans.name"));
		stage.setResizable(false);
		stage.show();
	}
	
	private Scene makeScene() {
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
		GridPane pane = FxUtils.makeGrid(Pos.CENTER, 5.0, 5.0);
		Button settingsButton = FxUtils.makeButton(TranslationKey.createAndGet("screen.menu.settings"), this::openSettings);
		settingsButton.setPrefWidth(150.0);
		Button refreshButton = FxUtils.makeButton(TranslationKey.createAndGet("account.window.refresh"), this::refreshPlayers);
		refreshButton.setPrefWidth(Constans.IDE ? 150.0 : 225.0);
		Button closeButton = FxUtils.makeButton(TranslationKey.createAndGet("account.window.close"), Platform::exit);
		closeButton.setPrefWidth(Constans.IDE ? 150.0 : 225.0);
		if (Constans.IDE) {
			pane.addRow(0, settingsButton, refreshButton, closeButton);
		} else {
			pane.addRow(0, refreshButton, closeButton);
		}
		box.getChildren().addAll(serverTree, pane);
		return new Scene(box, 450.0, 400.0);
	}
	
	private void openSettings() {
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
				Connection connection = new Connection(new ServerPacketHandler(DedicatedServer.this, NetworkSide.SERVER));
				pipeline.addLast("splitter", new ProtobufVarint32FrameDecoder());
				pipeline.addLast("decoder", new PacketDecoder());
				pipeline.addLast("prepender", new ProtobufVarint32LengthFieldPrepender());
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
		ServerPlayer player = new ServerPlayer(profile, new PlayerScore(profile), this);
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
				GamePlayer gamePlayer = this.game.getPlayerFor(player);
				if (gamePlayer != null) {
					this.game.removePlayer(gamePlayer, false);
				}
			}
		}
	}
	
	@Override
	public void tick() {
		this.playerList.tick();
	}
	
	@Override
	public void handlePacket(ServerPacket packet) {
		
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
		return tag;
	}
	
}
