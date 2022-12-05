package net.vgc.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import javafx.animation.Timeline;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.tags.CompoundTag;
import net.vgc.Constans;
import net.vgc.account.PlayerAccount;
import net.vgc.client.fx.ScreenScene;
import net.vgc.client.fx.Screenable;
import net.vgc.client.network.ClientPacketHandler;
import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.client.player.LocalPlayer;
import net.vgc.client.player.RemotePlayer;
import net.vgc.client.screen.LoadingScreen;
import net.vgc.client.screen.MenuScreen;
import net.vgc.client.screen.Screen;
import net.vgc.client.window.LoginWindow;
import net.vgc.common.application.GameApplication;
import net.vgc.game.Game;
import net.vgc.network.ConnectionHandler;
import net.vgc.network.NetworkSide;
import net.vgc.network.packet.account.ClientExitPacket;
import net.vgc.network.packet.server.ClientLeavePacket;
import net.vgc.player.GameProfile;
import net.vgc.util.Tickable;
import net.vgc.util.Util;
import net.vgc.util.exception.InvalidNetworkSideException;

/**
 *
 * @author Luis-st
 *
 */

public class Client extends GameApplication implements Tickable, Screenable {
	
	public static Client getInstance() {
		if (NetworkSide.CLIENT.isOn()) {
			return (Client) instance;
		}
		throw new InvalidNetworkSideException(NetworkSide.CLIENT);
	}
	
	private final Timeline ticker = Util.createTicker("ClientTicker", this);
	private final List<AbstractClientPlayer> players = Lists.newArrayList();
	private final ConnectionHandler serverHandler = new ConnectionHandler("virtual game collection server", () -> new ClientPacketHandler(Client.this, NetworkSide.CLIENT), (connection) -> {
		connection.send(new ClientLeavePacket(this.account));
	});
	private final ConnectionHandler accountHandler = new ConnectionHandler("account server", () -> new ClientPacketHandler(Client.this, NetworkSide.CLIENT), (connection) -> {
		connection.send(new ClientExitPacket(this.account));
	});
	private boolean instantLoading;
	private boolean safeLoading;
	private LoginWindow loginWindow;
	private PlayerAccount account;
	private LocalPlayer player;
	private ClientSettings settings;
	private String accountHost;
	private int accountPort;
	private Game game;
	
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
		} else if (set.has(safeLoading)) {
			this.safeLoading = set.valueOf(safeLoading);
			LOGGER.info("Use safe loading");
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
				LOGGER.warn("Fail to load settings from file {}, since tag {} is not an instance of CompoundTag, but it is a type of {}", settingsPath, settingsTag, settingsTag.getClass().getSimpleName());
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
		this.stage.setScene(screen.show());
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
			LOGGER.info("Add local player {}", player.getProfile().getName());
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
	
	@Nullable
	public AbstractClientPlayer getPlayer(GameProfile profile) {
		for (AbstractClientPlayer player : this.players) {
			if (player.getProfile().equals(profile)) {
				return player;
			}
		}
		return null;
	}
	
	public List<AbstractClientPlayer> getPlayers() {
		return this.players;
	}
	
	public List<AbstractClientPlayer> getPlayers(List<GameProfile> profiles) {
		List<AbstractClientPlayer> players = Lists.newArrayList();
		for (AbstractClientPlayer player : this.players) {
			if (profiles.contains(player.getProfile())) {
				players.add(player);
			}
		}
		if (profiles.size() > players.size()) {
			int i = profiles.size() - players.size();
			LOGGER.warn("Fail to get the player for {} game profile{}", i, i > 1 ? "s" : "");
		}
		return players;
	}
	
	public void addRemotePlayer(RemotePlayer player) {
		this.players.add(player);
		LOGGER.info("Add remote player {}", player.getProfile().getName());
	}
	
	public void removeRemotePlayer(RemotePlayer player) {
		this.players.remove(player);
		LOGGER.info("Remove remote player {}", player.getProfile().getName());
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
	
	public Game getGame() {
		return this.game;
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	@Override
	protected void handleStop() throws Exception {
		this.ticker.stop();
		this.accountHandler.close();
		this.serverHandler.close();
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
