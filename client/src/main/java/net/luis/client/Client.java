package net.luis.client;

import com.google.common.collect.Lists;
import javafx.animation.Timeline;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.luis.client.network.ClientPacketHandler;
import net.luis.client.player.AbstractClientPlayer;
import net.luis.client.player.LocalPlayer;
import net.luis.client.player.RemotePlayer;
import net.luis.client.screen.LoadingScreen;
import net.luis.client.screen.MenuScreen;
import net.luis.client.window.LoginWindow;
import net.luis.fx.ScreenScene;
import net.luis.fx.screen.AbstractScreen;
import net.luis.fx.screen.Screenable;
import net.luis.game.Game;
import net.luis.game.application.ApplicationType;
import net.luis.game.application.GameApplication;
import net.luis.game.player.GameProfile;
import net.luis.network.ConnectionHandler;
import net.luis.network.packet.account.ClientExitPacket;
import net.luis.network.packet.server.ClientLeavePacket;
import net.luis.utility.Tickable;
import net.luis.utility.Util;
import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.tags.CompoundTag;
import net.luis.utils.util.Utils;
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

public class Client extends GameApplication implements Tickable, Screenable {
	
	private final Timeline ticker = Util.createTicker("ClientTicker", this);
	private final List<AbstractClientPlayer> players = Lists.newArrayList();
	private final ClientPacketHandler packetHandler = new ClientPacketHandler(this);
	private boolean instantLoading;
	private boolean safeLoading;
	private boolean cachePasswordLocal;
	private LoginWindow loginWindow;
	private ClientAccount account;
	private final ConnectionHandler serverHandler = new ConnectionHandler("virtual game collection server", (connection) -> {
		connection.send(new ClientLeavePacket(this.account.name(), this.account.uuid()));
	});
	private final ConnectionHandler accountHandler = new ConnectionHandler("account server", (connection) -> {
		if (this.isLoggedIn()) {
			connection.send(new ClientExitPacket(this.account.name(), this.account.id(), this.account.uuid()));
		} else {
			connection.send(new ClientExitPacket("", -1, Utils.EMPTY_UUID));
		}
		
	});
	private String password;
	private LocalPlayer player;
	private ClientSettings settings;
	private String accountHost;
	private int accountPort;
	private Game game;
	
	public static Client getInstance() {
		if (GameApplication.getInstance() instanceof Client client) {
			return client;
		}
		throw new NullPointerException("The client instance is not yet available because the client has not yet been initialized");
	}
	
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
		OptionSpec<Boolean> cachePasswordLocal = parser.accepts("cachePasswordLocal").withRequiredArg().ofType(Boolean.class);
		OptionSet set = parser.parse(args);
		if (set.has(gameDir)) {
			this.gameDirectory = set.valueOf(gameDir).toPath();
			GameApplication.LOGGER.debug("Set game directory to {}", this.gameDirectory);
		} else {
			this.gameDirectory = new File(System.getProperty("user.home")).toPath().resolve("Desktop/run/client");
			GameApplication.LOGGER.warn("Fail to get game directory, use default directory: {}", this.gameDirectory);
		}
		if (!Files.exists(this.gameDirectory)) {
			Files.createDirectories(this.gameDirectory);
			GameApplication.LOGGER.debug("Create client directory");
		}
		if (set.has(resourceDir)) {
			this.resourceDirectory = set.valueOf(resourceDir).toPath();
			GameApplication.LOGGER.debug("Set resource directory to {}", this.resourceDirectory);
		} else {
			this.resourceDirectory = this.gameDirectory.resolve("assets");
			GameApplication.LOGGER.warn("No resource directory set, use the default directory {}", this.gameDirectory);
		}
		if (!Files.exists(this.resourceDirectory)) {
			Files.createDirectories(this.resourceDirectory);
			GameApplication.LOGGER.debug("Create resource directory");
		}
		if (set.has(accountHost)) {
			this.accountHost = set.valueOf(accountHost);
		} else {
			this.accountHost = "localhost";
			GameApplication.LOGGER.warn("Fail to get account server host, use default host: 127.0.0.1");
		}
		if (set.has(accountPort)) {
			this.accountPort = set.valueOf(accountPort);
		} else {
			this.accountPort = 8081;
			GameApplication.LOGGER.warn("Fail to get account server port, use default host: 8081");
		}
		if (set.has(instantLoading)) {
			if (this.instantLoading = set.valueOf(instantLoading)) {
				GameApplication.LOGGER.debug("Try instant loading");
			} else if (set.has(safeLoading)) {
				this.safeLoading = set.valueOf(safeLoading);
				GameApplication.LOGGER.info("Use safe loading");
			}
		} else if (set.has(safeLoading)) {
			this.safeLoading = set.valueOf(safeLoading);
			GameApplication.LOGGER.info("Use safe loading");
		}
		if (set.has(cachePasswordLocal)) {
			this.cachePasswordLocal = set.valueOf(cachePasswordLocal);
		} else {
			this.cachePasswordLocal = true;
			GameApplication.LOGGER.info("Password caching was not specified, use default value: true");
		}
	}
	
	@Override
	public void load() {
		Path settingsPath = this.gameDirectory.resolve("settings.data");
		if (Files.exists(settingsPath)) {
			Tag settingsTag = Tag.load(settingsPath);
			if (settingsTag instanceof CompoundTag tag) {
				this.settings = new ClientSettings(tag);
				GameApplication.LOGGER.debug("Load settings");
			} else {
				GameApplication.LOGGER.warn("Fail to load settings from file {}, since tag {} is not an instance of CompoundTag, but it is a type of {}", settingsPath, settingsTag, settingsTag.getClass().getSimpleName());
			}
		} else {
			this.settings = new ClientSettings();
			GameApplication.LOGGER.info("No settings present, use default settings");
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
	public void setScreen(AbstractScreen screen) {
		screen.init();
		if (screen.title != null) {
			this.stage.setTitle(screen.title);
		} else if (!this.stage.getTitle().trim().isEmpty()) {
			this.stage.setTitle("");
		}
		this.stage.setScene(screen.show());
	}
	
	@Override
	public ApplicationType getApplicationType() {
		return ApplicationType.CLIENT;
	}
	
	@Override
	protected Timeline getTicker() {
		return this.ticker;
	}
	
	public ClientPacketHandler getPacketHandler() {
		return this.packetHandler;
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
	
	public ClientAccount getAccount() {
		return this.account;
	}
	
	public void login(String name, int id, String mail, UUID uuid, boolean guest) {
		GameApplication.LOGGER.info("Login with account: {}#{}", name, id);
		this.account = new ClientAccount(name, id, mail, uuid, guest);
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		if (this.cachePasswordLocal) {
			this.password = password;
		} else {
			this.password = "";
			GameApplication.LOGGER.debug("The password is not cached local because it is disabled");
		}
	}
	
	public boolean isPasswordCachedLocal() {
		return this.cachePasswordLocal;
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
			GameApplication.LOGGER.info("Add local player {}", player.getProfile().getName());
			this.player = player;
			this.players.add(player);
		} else {
			this.removePlayer();
		}
	}
	
	public void removePlayer() {
		if (this.player != null) {
			GameApplication.LOGGER.info("Remove local player");
			this.player = null;
		}
		GameApplication.LOGGER.info("Remove all remote players");
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
			GameApplication.LOGGER.warn("Fail to get the player for {} game profile{}", i, i > 1 ? "s" : "");
		}
		return players;
	}
	
	public void addRemotePlayer(RemotePlayer player) {
		this.players.add(player);
		GameApplication.LOGGER.info("Add remote player {}", player.getProfile().getName());
	}
	
	public void removeRemotePlayer(RemotePlayer player) {
		this.players.remove(player);
		GameApplication.LOGGER.info("Remove remote player {}", player.getProfile().getName());
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
	protected void handleStop() {
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
			GameApplication.LOGGER.info("Restore settings to default, since they're not present");
		}
		Tag.save(settingsPath, this.settings.serialize());
		GameApplication.LOGGER.debug("Save settings");
	}
	
}
