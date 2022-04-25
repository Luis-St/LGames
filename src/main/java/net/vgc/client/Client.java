package net.vgc.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.common.collect.Lists;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.vgc.Constans;
import net.vgc.account.PlayerAccount;
import net.vgc.client.fx.ScreenScene;
import net.vgc.client.fx.Screenable;
import net.vgc.client.network.ClientPacketListener;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.client.player.LocalPlayer;
import net.vgc.client.player.RemotePlayer;
import net.vgc.client.screen.LoadingScreen;
import net.vgc.client.screen.MenuScreen;
import net.vgc.client.screen.Screen;
import net.vgc.client.window.LoginWindow;
import net.vgc.common.application.GameApplication;
import net.vgc.data.tag.Tag;
import net.vgc.data.tag.tags.CompoundTag;
import net.vgc.network.ConnectionHandler;
import net.vgc.network.InvalidNetworkSideException;
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.Packet;
import net.vgc.network.packet.account.ClientExitPacket;
import net.vgc.network.packet.client.ClientScreenPacket;
import net.vgc.network.packet.server.ClientLeavePacket;
import net.vgc.util.Tickable;
import net.vgc.util.Util;

public class Client extends GameApplication<ClientPacketListener> implements Tickable, Screenable {
	
	public static Client getInstance() {
		if (NetworkSide.CLIENT.isOn()) {
			return (Client) instance;
		}
		throw new InvalidNetworkSideException(NetworkSide.CLIENT);
	}
	
	protected final Timeline ticker = Util.createTicker("ClientTicker", this);
	protected final EventLoopGroup serverGroup = NATIVE ? new EpollEventLoopGroup() : new NioEventLoopGroup();
	protected final EventLoopGroup accountGroup = NATIVE ? new EpollEventLoopGroup() : new NioEventLoopGroup();
	protected final List<AbstractClientPlayer> players = Lists.newArrayList();
	protected final ConnectionHandler serverHandler = new ConnectionHandler("virtual game collection server", new ClientPacketListener(this, NetworkSide.CLIENT), (connection) -> {
		connection.send(new ClientLeavePacket(this.account));
	});
	protected final ConnectionHandler accountHandler = new ConnectionHandler("account server", new ClientPacketListener(this, NetworkSide.CLIENT), (connection) -> {
		connection.send(new ClientExitPacket(this.account));
	});
	protected boolean instantLoading;
	protected boolean safeLoading;
	protected LoginWindow loginWindow;
	protected PlayerAccount account;
	protected LocalPlayer player;
	protected ClientSettings settings;
	protected String accountHost;
	protected int accountPort;
	
	@Override
	protected void handleStart(String[] args) throws Exception {
		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();
		OptionSpec<File> gameDir = parser.accepts("gameDir").withRequiredArg().ofType(File.class);
		OptionSpec<File> resourceDir = parser.accepts("resourceDir").withRequiredArg().ofType(File.class);
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
			LOGGER.warn("Fail to get game directory, use default directory: {}", this.gameDirectory);
		}
		if (!Files.exists(this.gameDirectory)) {
			Files.createDirectories(this.gameDirectory);
			LOGGER.debug("Create client directory");
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
		if (set.has(accountHost)) {
			this.accountHost = set.valueOf(accountHost);
		} else {
			this.accountHost = "localhost";
			LOGGER.warn("Fail to get account server host, use default host: 127.0.0.1");
		}
		if (set.has(accountPort)) {
			this.accountPort = set.valueOf(accountPort);
		} else {
			this.accountPort = 8081;
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
	public void load() throws IOException {
		Path settingsPath = this.gameDirectory.resolve("settings.data");
		if (Files.exists(settingsPath)) {
			Tag settingsTag = Tag.load(settingsPath);
			if (settingsTag instanceof CompoundTag tag) {
				this.settings = new ClientSettings(tag);
				LOGGER.debug("Load settings");
			} else {
				LOGGER.warn("Fail to load settings from file {}, since Tag {} is not an instance of CompoundTag, but it is a type of {}", settingsPath, settingsTag, settingsTag.getClass().getSimpleName());
			}
		} else {
			this.settings = new ClientSettings();
			LOGGER.info("No settings present, use default settings");
		}
	}
	
	@Override
	protected void setupStage() {
		this.setScreen(new LoadingScreen());
		this.stage.show();
		if (this.isInstantLoading()) {
			this.setScreen(new MenuScreen());
			this.stage.centerOnScreen();
		}
	}
	
	@Override
	public void tick() {
		if (this.stage != null && this.stage.getScene() instanceof ScreenScene screenScene) {
			screenScene.tick();
		}
		this.players.forEach(AbstractClientPlayer::tick);
	}
	
	@Override
	public void setScreen(Screen screen) {
		screen.init();
		if (screen.title != null) {
			this.stage.setTitle(screen.title);
		} else if (!this.stage.getTitle().trim().isEmpty()) {
			this.stage.setTitle("");
		}
		this.setScene(screen.show());
	}
	
	protected void setScene(Scene scene) {
		if (scene instanceof ScreenScene screenScene) {
			screenScene.setInputListeners();
		}
		this.stage.setScene(scene);
	}
	
	@Override
	protected String getThreadName() {
		return "client";
	}
	
	@Override
	protected String getName() {
		return "virtual game collection";
	}
	
	@Override
	protected String getVersion() {
		return Constans.Client.VERSION;
	}
	
	@Override
	public NetworkSide getNetworkSide() {
		return NetworkSide.CLIENT;
	}
	
	@Override
	public void handlePacket(Packet<ClientPacketListener> packet) {
		if (packet instanceof ClientScreenPacket screenUpdate) {
			Scene scene = this.stage.getScene();
			if (scene != null && scene instanceof ScreenScene screenScene) {
				Screen screen = screenScene.getScreen();
				if (screen != null && (screenUpdate.getScreens().contains(screen.getClass())) || screenUpdate.receiveAllScreens()) {
					screen.handlePacket(screenUpdate);
				}
			}
		}
	}
	
	@Override
	protected Timeline getTicker() {
		return this.ticker;
	}
	
	public boolean isInstantLoading() {
		return this.instantLoading;
	}
	
	public boolean isSafeLoading() {
		return this.safeLoading;
	}
	
	public LoginWindow getLoginWindow() {
		return this.loginWindow;
	}
	
	public void setLoginWindow(LoginWindow loginWindow) {
		this.loginWindow = loginWindow;
	}
	
	public PlayerAccount getAccount() {
		return this.account;
	}
	
	public void login(PlayerAccount account) {
		LOGGER.info("Login with account: {}", account);
		this.account = account;
	}
	
	public void logout() {
		this.account = null;
	}
	
	public boolean isLoggedIn() {
		return this.account != null;
	}
	
	public LocalPlayer getPlayer() {
		return this.player;
	}
	
	public void setPlayer(LocalPlayer player) {
		if (this.player == null && player != null) {
			LOGGER.info("Add local player {}", player.getGameProfile().getName());
			this.player = player;
			this.players.add(player);
		} else {
			this.removePlayer();
		}
	}
	
	public void removePlayer() {
		if (this.player != null) {
			LOGGER.info("Remove local player");
			this.player = null;
		}
		LOGGER.info("Remove all remote players");
		this.players.clear();
		this.serverHandler.close();
	}
	
	public List<AbstractClientPlayer> getPlayers() {
		return this.players;
	}
	
	public void addRemotePlayer(RemotePlayer player) {
		this.players.add(player);
		LOGGER.info("Add remote player {}", player.getGameProfile().getName());
	}
	
	public void removeRemotePlayer(RemotePlayer player) {
		this.players.remove(player);
		LOGGER.info("Remove remote player {}", player.getGameProfile().getName());
	}
	
	public ClientSettings getSettings() {
		return this.settings;
	}
	
	public ConnectionHandler getServerHandler() {
		return this.serverHandler;
	}
	
	public ConnectionHandler getAccountHandler() {
		return this.accountHandler;
	}
	
	public String getAccountHost() {
		return this.accountHost;
	}
	
	public int getAccountPort() {
		return this.accountPort;
	}
	
	@Override
	protected void handleStop() throws Exception {
		this.ticker.stop();
		this.accountHandler.disconnect();
		this.serverHandler.disconnect();
	}
	
	@Override
	public void save() throws IOException {
		Path settingsPath = this.gameDirectory.resolve("settings.data");
		if (!Files.exists(settingsPath)) {
			Files.createDirectories(settingsPath.getParent());
			Files.createFile(settingsPath);
		}
		if (this.settings == null) {
			this.settings = new ClientSettings();
			LOGGER.info("Restore settings to default, since they're not present");
		}
		Tag.save(settingsPath, this.settings.serialize());
		LOGGER.debug("Save settings");
	}
	
}
