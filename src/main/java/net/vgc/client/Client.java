package net.vgc.client;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.vgc.Constans;
import net.vgc.client.fx.ScreenScene;
import net.vgc.client.fx.Screenable;
import net.vgc.client.network.ClientPacketListener;
import net.vgc.client.screen.LoadingScreen;
import net.vgc.client.screen.MenuScreen;
import net.vgc.client.screen.Screen;
import net.vgc.common.LaunchState;
import net.vgc.common.application.GameApplication;
import net.vgc.language.LanguageProvider;
import net.vgc.network.Connection;
import net.vgc.network.InvalidNetworkSideException;
import net.vgc.network.Network;
import net.vgc.network.NetworkSide;
import net.vgc.network.PacketDecoder;
import net.vgc.network.PacketEncoder;
import net.vgc.server.account.PlayerAccount;
import net.vgc.util.Tickable;
import net.vgc.util.Util;

public class Client extends GameApplication  implements Tickable, Screenable {
	
	protected static Client instance;
	
	public static Client getInstance() {
		if (NetworkSide.CLIENT.isOn()) {
			return instance;
		}
		throw new InvalidNetworkSideException(NetworkSide.CLIENT);
	}
	
	protected final Timeline ticker = Util.createTicker("ClientTicker", () -> {
		Client.getInstance().tick();
	});
	protected final EventLoopGroup serverGroup = NATIVE ? new EpollEventLoopGroup() : new NioEventLoopGroup();
	protected final EventLoopGroup accountGroup = NATIVE ? new EpollEventLoopGroup() : new NioEventLoopGroup();
	protected Path gameDirectory; 
	protected Path resourceDirectory;
	protected LaunchState launchState = LaunchState.UNKNOWN;
	protected Scene currentScene;
	protected Scene previousScene;
	protected boolean instantLoading;
	protected boolean safeLoading;
	protected Random rng;
	protected PlayerAccount account;
	protected String serverHost;
	protected int serverPort;
	protected String accountHost;
	protected int accountPort;
	protected Channel serverChannel;
	protected Connection serverConnection;
	protected Channel accountChannel;
	protected Connection accountConnection;
	
	@Override
	public void init() throws Exception {
		super.init();
		LOGGER.info("Initial virtual game collection");
		instance = this;
		this.ticker.play();
		this.rng = new Random(System.currentTimeMillis());
	}
	
	@Override
	public void start(String[] args) throws Exception {
		LOGGER.info("Starting virtual game collection");
		this.launchState = LaunchState.STARTING;
		Network.INSTANCE.setNetworkSide(NetworkSide.CLIENT);
		this.handleStart(args);
		LanguageProvider.INSTANCE.load();
		this.stage.setScene(new Scene(new Group(), 400, 400));
		this.setScreen(new LoadingScreen());
		this.stage.show();
		if (this.isInstantLoading()) {
			this.setScreen(new MenuScreen());
			this.stage.centerOnScreen();
		}
		this.launchState = LaunchState.STARTED;
		LOGGER.info("Successfully start of virtual game collection with version {}", Constans.Client.VERSION);
	}
	
	protected void handleStart(String[] args) throws Exception { // TODO: create Loading Steps, which are load from 0 til 1
		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();
		OptionSpec<File> gameDir = parser.accepts("gameDir").withRequiredArg().ofType(File.class);
		OptionSpec<File> resourceDir = parser.accepts("resourceDir").withRequiredArg().ofType(File.class);
		OptionSpec<String> serverHost = parser.accepts("serverHost").withRequiredArg().ofType(String.class);
		OptionSpec<Integer> serverPort = parser.accepts("serverPort").withRequiredArg().ofType(Integer.class);
		OptionSpec<String> accountHost = parser.accepts("accountHost").withRequiredArg().ofType(String.class);
		OptionSpec<Integer> accountPort = parser.accepts("accountPort").withRequiredArg().ofType(Integer.class);
		OptionSpec<Boolean> instantLoading = parser.accepts("instantLoading").withRequiredArg().ofType(Boolean.class);
		OptionSpec<Boolean> safeLoading = parser.accepts("safeLoading").withRequiredArg().ofType(Boolean.class);
		OptionSet set = parser.parse(args);
		if (set.has(gameDir)) {
			this.gameDirectory = set.valueOf(gameDir).toPath();
			LOGGER.debug("Set game directory to {}", this.gameDirectory);
		} else {
			this.gameDirectory = new File(System.getProperty("user.home")).toPath().resolve("Desktop/run/client");
			LOGGER.warn("Fail to get game directory, use default directory: {}", this.gameDirectory); // TODO: use ErrorWindow & interrupt loading while open/not choose
		}
		if (!Files.exists(this.gameDirectory)) {
			Files.createDirectories(this.gameDirectory);
		}
		if (set.has(resourceDir)) {
			this.resourceDirectory = set.valueOf(resourceDir).toPath();
			LOGGER.debug("Set resource directory to {}", this.resourceDirectory);
		} else {
			this.resourceDirectory = this.gameDirectory.resolve("assets");
			LOGGER.warn("No resource directory set, use the default directory {}", this.gameDirectory);
		}
		if (!Files.exists(this.gameDirectory)) {
			Files.createDirectories(this.gameDirectory);
			LOGGER.debug("Create client directory");
		}
		if (set.has(serverHost)) {
			this.serverHost = set.valueOf(serverHost);
		} else {
			this.serverHost = "localhost";
			LOGGER.warn("Fail to get server host, use default host: 127.0.0.1");
		}
		if (set.has(serverPort)) {
			this.serverPort = set.valueOf(serverPort);
		} else {
			this.serverPort = 8080;
			LOGGER.warn("Fail to get server port, use default host: 8080");
		}
		if (set.has(accountHost)) {
			this.accountHost = set.valueOf(accountHost);
		} else {
			this.accountHost = "localhost";
			LOGGER.warn("Fail to get account server host, use default host: 127.0.0.1");
		}
		if (set.has(accountPort)) {
			this.accountPort = set.valueOf(accountPort);
		} else {
			this.serverPort = 8081;
			LOGGER.warn("Fail to get account server port, use default host: 8081");
		}
		if (set.has(instantLoading)) {
			if (this.instantLoading = set.valueOf(instantLoading)) {
				LOGGER.debug("Try instant loading");
			} else if (set.has(safeLoading)) {
				this.safeLoading = set.valueOf(safeLoading);
				LOGGER.info("Use safe loading");
			}
		}
	}
	
	@Override
	public void tick() { // REWORK
		if (this.stage != null && this.stage.getScene() instanceof ScreenScene screenScene) {
			screenScene.tick();
		}
	}
	
	public void connectServer() {
		// TODO Auto-generated method stub
		
	}
	
	public void disconnectServer() {
		// TODO Auto-generated method stub
		
	}
	
	public Connection getServerConnection() {
		return this.serverConnection;
	}
	
	public boolean isServerConnected() {
		return this.serverConnection != null && this.serverConnection.isConnected();
	}
	
	public void connectAccount() {
		try {
			this.accountConnection = new Connection(new ClientPacketListener(this, NetworkSide.CLIENT));
			this.accountChannel = new Bootstrap().group(this.accountGroup).channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel channel) throws Exception {
					ChannelPipeline pipeline = channel.pipeline();
					pipeline.addLast("decoder", new PacketDecoder());
					pipeline.addLast("encoder", new PacketEncoder());
					pipeline.addLast("handler", Client.this.accountConnection);
				}
			}).connect(this.accountHost, this.accountPort).sync().channel();
			LOGGER.info("Start connection to account server on host {} with port {}", this.accountHost, this.accountPort);
		} catch (Exception e) {
			LOGGER.error("Fail to start connection to account server on host {} with port {}, since {}", this.accountHost, this.accountPort, e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	public void disconnectAccount() {
		if (this.launchState == LaunchState.STARTED || this.launchState == LaunchState.STOPPING) {
			this.accountConnection = null;
			if (this.accountChannel != null) {
				this.accountChannel.closeFuture().syncUninterruptibly();
			}
			if (this.accountGroup != null) {
				this.accountGroup.shutdownGracefully();
			}
		} else {
			LOGGER.warn("Unable to disconnect from account server");
		}
	}
	
	public Connection getAccountConnection() {
		return this.accountConnection;
	}
	
	public boolean isAccountConnected() {
		return this.accountConnection != null && this.accountConnection.isConnected();
	}
	
	@Override
	protected String getThreadName() {
		return "client";
	}
	
	public Path getGameDirectory() {
		return this.gameDirectory;
	}
	
	public Path getResourceDirectory() {
		return this.resourceDirectory;
	}
	
	public LaunchState getLaunchState() {
		return this.launchState;
	}
	
	public boolean isRunning() {
		return this.launchState == LaunchState.STARTED;
	}
	
	public boolean isInstantLoading() {
		return this.instantLoading;
	}
	
	public boolean isSafeLoading() {
		return this.safeLoading;
	}
	
	public PlayerAccount getAccount() {
		return this.account;
	}
	
	public void setAccount(PlayerAccount account) {
		if (account != null && account != PlayerAccount.UNKNOWN) {
			LOGGER.info("Login with account: {}", account);
			this.account = account;
		}
	}
	
	public boolean isLoggedIn() {
		return this.account != null;
	}
	
	@Override
	public void setScreen(Screen screen) {
		this.initScreen(screen);
		this.setScene(screen.show());
		LOGGER.debug("Update Screen to {}", screen.getClass().getSimpleName());
	}
	
	protected void initScreen(Screen screen) {
		screen.init();
		if (screen.title != null) {
			this.stage.setTitle(screen.title);
		} else if (!this.stage.getTitle().trim().isEmpty()) {
			this.stage.setTitle("");
		}
		if (screen.shouldCenter) {
			this.stage.centerOnScreen();
		}
	}
	
	protected void setScene(Scene scene) {
		this.previousScene = this.currentScene;
		if (scene instanceof ScreenScene screenScene) {
			screenScene.setInputListeners();
		}
		this.currentScene = scene;
		this.stage.setScene(scene);
	}
	
	public void exit() {
		LOGGER.info("Exit virtual game collection");
		Platform.exit();
	}
	
	@Override
	public void stop() throws Exception {
		LOGGER.info("Stopping virtual game collection");
		this.launchState = LaunchState.STOPPING;
		this.handleStop();
		this.launchState = LaunchState.STOPPED;
		LOGGER.info("Successfully stopping virtual game collection");
	}
	
	protected void handleStop() {
		this.ticker.stop();
		this.disconnectAccount();
		this.disconnectServer();
	}
	
}
