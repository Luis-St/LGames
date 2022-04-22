package net.vgc.server;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import javafx.animation.Timeline;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.vgc.Constans;
import net.vgc.common.application.GameApplication;
import net.vgc.language.Language;
import net.vgc.language.LanguageProvider;
import net.vgc.language.Languages;
import net.vgc.network.InvalidNetworkSideException;
import net.vgc.network.NetworkSide;
import net.vgc.server.dedicated.DedicatedServer;
import net.vgc.server.network.ServerPacketListener;
import net.vgc.util.Tickable;
import net.vgc.util.Util;

public class Server extends GameApplication<ServerPacketListener> implements Tickable {
	
	public static Server getInstance() {
		if (NetworkSide.SERVER.isOn()) {
			return (Server) instance;
		}
		throw new InvalidNetworkSideException(NetworkSide.SERVER);
	}
	
	protected final Timeline ticker = Util.createTicker("ServerTicker", this);
	protected String host;
	protected int port;
	protected UUID admin;
	protected DedicatedServer server;
	
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
				LOGGER.warn("Fail to create admin id, since the given id {} does not have the correct id formate", string);
			}
		}
	}
	
	@Override
	protected void setupStage() {
		try {
			this.server = new DedicatedServer(this.host, this.port, this.gameDirectory);
			this.server.init();
			if (this.admin != null) {
				if (this.server.getAdmin() == null) {
					this.server.setAdmin(this.admin);
				} else if (!this.admin.equals(this.server.getAdmin())) {
					LOGGER.warn("Fail to set admin to {}, since the admin is already set to {}", this.admin, this.server.getAdmin());
				}
			}
			this.server.displayServer(this.stage);
		} catch (Exception e) {
			LOGGER.error("Something went wrong while creating virtual game collection server");
			throw new RuntimeException("Fail to creating virtual game collection server", e);
		}
		try {
			this.server.startServer();
		} catch (Exception e) {
			LOGGER.error("Something went wrong while launching virtual game collection server");
			throw new RuntimeException("Fail to launch virtual game collection server", e);
		}
	}
	
	@Override
	public void tick() {
		this.server.tick();
	}
	
	@Override
	protected String getThreadName() {
		return "server";
	}
	
	@Override
	protected String getName() {
		return "virtual game collection server";
	}
	
	@Override
	protected String getVersion() {
		return Constans.Server.VERSION;
	}
	
	@Override
	public NetworkSide getNetworkSide() {
		return NetworkSide.SERVER;
	}
	
	@Override
	protected Timeline getTicker() {
		return this.ticker;
	}
	
	public Path getGameDirectory() {
		return this.gameDirectory;
	}
	
	public Path getResourceDirectory() {
		return this.resourceDirectory;
	}
	
	public DedicatedServer getServer() {
		return this.server;
	}
	
	@Override
	protected void handleStop() throws Exception {
		this.ticker.stop();
		this.server.stopServer();
		this.server = null;
	}
	
}
