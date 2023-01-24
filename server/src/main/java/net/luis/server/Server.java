package net.luis.server;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.luis.language.Language;
import net.luis.language.LanguageProvider;
import net.luis.language.Languages;
import net.luis.language.TranslationKey;
import net.luis.Constants;
import net.luis.application.ApplicationType;
import net.luis.application.GameApplication;
import net.luis.fxutils.FxUtils;
import net.luis.game.Game;
import net.luis.game.player.GamePlayer;
import net.luis.game.score.PlayerScore;
import net.luis.network.Connection;
import net.luis.network.packet.PacketDecoder;
import net.luis.network.packet.PacketEncoder;
import net.luis.game.player.GameProfile;
import net.luis.server.network.ServerPacketHandler;
import net.luis.server.player.ServerPlayer;
import net.luis.server.players.PlayerList;
import net.luis.utility.ExceptionHandler;
import net.luis.utility.Tickable;
import net.luis.utility.Util;
import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.tags.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class Server extends GameApplication implements Tickable {
	
	private final Timeline ticker = Util.createTicker("ServerTicker", this);
	private final EventLoopGroup group = NATIVE ? new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("connection #%d").setUncaughtExceptionHandler(new ExceptionHandler()).build())
			: new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("connection #%d").setUncaughtExceptionHandler(new ExceptionHandler()).build());
	private final List<Channel> channels = Lists.newArrayList();
	private final List<Connection> connections = Lists.newArrayList();
	private final ServerPacketHandler packetHandler = new ServerPacketHandler(this);
	private final PlayerList playerList = new PlayerList(this);
	private String host;
	private int port;
	private UUID admin;
	private ServerPlayer adminPlayer;
	private TreeItem<String> playersTreeItem;
	private Game game;
	
	public static Server getInstance() {
		if (GameApplication.getInstance() instanceof Server server) {
			return server;
		}
		throw new NullPointerException("The server instance is not yet available because the server has not yet been initialized");
	}
	
	@Override
	protected void handleStart(String[] args) throws Exception {
		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();
		OptionSpec<File> gameDir = parser.accepts("gameDir").withRequiredArg().ofType(File.class);
		OptionSpec<File> resourceDir = parser.accepts("resourceDir").withRequiredArg().ofType(File.class);
		OptionSpec<String> host = parser.accepts("host").withRequiredArg().ofType(String.class);
		OptionSpec<Integer> port = parser.accepts("port").withRequiredArg().ofType(Integer.class);
		OptionSpec<String> language = parser.accepts("language").withRequiredArg().ofType(String.class);
		OptionSpec<String> admin = parser.accepts("admin").withRequiredArg().ofType(String.class);
		OptionSet set = parser.parse(args);
		if (set.has(gameDir)) {
			this.gameDirectory = set.valueOf(gameDir).toPath();
			LOGGER.debug("Set game directory to {}", this.gameDirectory);
		} else {
			this.gameDirectory = new File(System.getProperty("user.home")).toPath().resolve("Desktop/run/client");
			LOGGER.warn("Fail to get game directory, use default directory: {}", this.gameDirectory);
		}
		if (!Files.exists(this.gameDirectory)) {
			Files.createDirectories(this.gameDirectory);
			LOGGER.debug("Create server directory");
		}
		if (set.has(resourceDir)) {
			this.resourceDirectory = set.valueOf(resourceDir).toPath();
			LOGGER.debug("Set resource directory to {}", this.resourceDirectory);
		} else {
			this.resourceDirectory = this.gameDirectory.resolve("assets");
			LOGGER.warn("No resource directory set, use the default directory {}", this.gameDirectory);
		}
		if (!Files.exists(this.resourceDirectory)) {
			Files.createDirectories(this.resourceDirectory);
			LOGGER.debug("Create resource directory");
		}
		if (set.has(host)) {
			this.host = set.valueOf(host);
		} else {
			this.host = "localhost";
			LOGGER.warn("Fail to get host, use default host: 127.0.0.1");
		}
		if (set.has(port)) {
			this.port = set.valueOf(port);
		} else {
			this.port = 8081;
			LOGGER.warn("Fail to get port, use default port: 8081");
		}
		if (set.has(language)) {
			Language lang = Languages.fromFileName(set.valueOf(language));
			if (lang != null) {
				LanguageProvider.INSTANCE.setCurrentLanguage(lang);
			} else {
				LOGGER.info("Fail to get language, since the {} language does not exists or is not load", set.valueOf(language));
			}
		}
		if (set.has(admin)) {
			String string = set.valueOf(admin);
			try {
				this.admin = UUID.fromString(string);
			} catch (IllegalArgumentException e) {
				LOGGER.warn("Fail to create admin id, since the given id {} does not have the correct id format", string);
			}
		}
	}
	
	@Override
	public void load() throws IOException {
		if (!Files.exists(this.gameDirectory)) {
			Files.createDirectories(this.gameDirectory.getParent());
			LOGGER.debug("Create server directory");
		}
		Path path = this.gameDirectory.resolve("server.data");
		if (Files.exists(path)) {
			Tag loadTag = Tag.load(path);
			if (!(loadTag instanceof CompoundTag)) {
				LOGGER.warn("Fail to load server from {}, since tag {} is not a instance of CompoundTag but it is a tag of type {}", path, loadTag, loadTag.getClass().getSimpleName());
			}
		}
	}
	
	@Override
	protected void setupStage() {
		try {
			this.stage.setScene(this.makeScene());
			this.stage.setTitle(TranslationKey.createAndGet("server.constans.name"));
			this.stage.setResizable(false);
			this.stage.show();
		} catch (Exception e) {
			LOGGER.error("Something went wrong while creating virtual game collection server");
			throw new RuntimeException("Fail to creating virtual game collection server", e);
		}
		try {
			new ServerBootstrap().group(this.group).channel(NATIVE ? EpollServerSocketChannel.class : NioServerSocketChannel.class).childHandler(new ChannelInitializer<>() {
				@Override
				protected void initChannel(Channel channel) {
					ChannelPipeline pipeline = channel.pipeline();
					Connection connection = new Connection();
					pipeline.addLast("splitter", new ProtobufVarint32FrameDecoder());
					pipeline.addLast("decoder", new PacketDecoder());
					pipeline.addLast("prepender", new ProtobufVarint32LengthFieldPrepender());
					pipeline.addLast("encoder", new PacketEncoder());
					pipeline.addLast("handler", connection);
					Server.this.channels.add(channel);
					Server.this.connections.add(connection);
					LOGGER.debug("Client connected with address {}", channel.remoteAddress().toString().replace("/", ""));
				}
			}).localAddress(this.host, this.port).bind().syncUninterruptibly().channel();
			LOGGER.info("Launch dedicated virtual game collection server on host {} with port {}", this.host, this.port);
		} catch (Exception e) {
			LOGGER.error("Something went wrong while launching virtual game collection server");
			throw new RuntimeException("Fail to launch virtual game collection server", e);
		}
	}
	
	private Scene makeScene() {
		VBox box = new VBox();
		TreeView<String> serverTree = new TreeView<>();
		TreeItem<String> treeItem = new TreeItem<>(TranslationKey.createAndGet("server.window.server"));
		treeItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("server.window.server_host", this.host)));
		treeItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("server.window.server_port", this.port)));
		treeItem.getChildren().add(new TreeItem<>(TranslationKey.createAndGet("server.window.server_admin", this.admin)));
		this.playersTreeItem = new TreeItem<>(TranslationKey.createAndGet("server.window.players"));
		for (ServerPlayer player : this.playerList.getPlayers()) {
			this.playersTreeItem.getChildren().add(player.display());
		}
		treeItem.getChildren().add(this.playersTreeItem);
		serverTree.setRoot(treeItem);
		serverTree.setShowRoot(Constants.DEBUG);
		GridPane pane = FxUtils.makeGrid(Pos.CENTER, 5.0, 5.0);
		Button settingsButton = FxUtils.makeButton(TranslationKey.createAndGet("screen.menu.settings"), this::openSettings);
		settingsButton.setPrefWidth(150.0);
		Button refreshButton = FxUtils.makeButton(TranslationKey.createAndGet("account.window.refresh"), this::refreshPlayers);
		refreshButton.setPrefWidth(Constants.IDE ? 150.0 : 225.0);
		Button closeButton = FxUtils.makeButton(TranslationKey.createAndGet("account.window.close"), Platform::exit);
		closeButton.setPrefWidth(Constants.IDE ? 150.0 : 225.0);
		if (Constants.IDE) {
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
	
	@Override
	public void tick() {
		this.playerList.tick();
	}
	
	@Override
	public ApplicationType getApplicationType() {
		return ApplicationType.SERVER;
	}
	
	@Override
	protected Timeline getTicker() {
		return this.ticker;
	}
	
	public ServerPacketHandler getPacketHandler() {
		return this.packetHandler;
	}
	
	public Path getGameDirectory() {
		return this.gameDirectory;
	}
	
	public Path getResourceDirectory() {
		return this.resourceDirectory;
	}
	
	public UUID getAdmin() {
		return this.admin;
	}
	
	public boolean isAdmin(ServerPlayer player) {
		return player.getProfile().getUUID().equals(this.admin);
	}
	
	@Nullable
	public ServerPlayer getAdminPlayer() {
		if (this.adminPlayer == null) {
			this.adminPlayer = this.playerList.getPlayer(this.admin);
		}
		return this.adminPlayer;
	}
	
	public PlayerList getPlayerList() {
		return this.playerList;
	}
	
	public Game getGame() {
		return this.game;
	}
	
	public void setGame(Game game) {
		this.game = game;
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
	public void save() {
		Tag.save(this.gameDirectory.resolve("server.data"), new CompoundTag());
		LOGGER.debug("Save server data");
	}
	
	@Override
	protected void handleStop() {
		this.ticker.stop();
		this.adminPlayer = null;
		this.playerList.removeAllPlayers();
		this.connections.clear();
		this.channels.forEach(Channel::close);
		this.channels.clear();
		this.group.shutdownGracefully();
	}
	
}
