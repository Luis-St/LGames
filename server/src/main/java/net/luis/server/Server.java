package net.luis.server;

import javafx.stage.Stage;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.luis.game.GameManager;
import net.luis.game.application.Application;
import net.luis.game.application.ApplicationType;
import net.luis.game.application.FxApplication;
import net.luis.game.application.GameApplication;
import net.luis.game.resources.ResourceManager;
import net.luis.netcore.network.NetworkInstance;
import net.luis.netcore.network.ServerInstance;
import net.luis.server.network.ServerPacketHandler;
import net.luis.server.players.ServerPlayerList;
import net.luis.utils.util.LazyInstantiation;
import net.luis.utils.util.LazyLoad;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

@Application(ApplicationType.SERVER)
public class Server extends FxApplication implements GameApplication {
	
	private static final Logger LOGGER = LogManager.getLogger(Server.class);
	
	private final LazyLoad<NetworkInstance> networkInstance = new LazyLoad<>(() -> {
		return new ServerInstance(this.host, this.port);
	});
	private final ServerPlayerList playerList = new ServerPlayerList(this);
	private final GameManager gameManager = new GameManager();
	private String host;
	private int port;
	
	public Server(ApplicationType type, ResourceManager resourceManager, Stage stage) {
		super(type, resourceManager, stage, new MainScreen(), false);
	}
	
	public static @NotNull Server getInstance() {
		if (FxApplication.getInstance() instanceof Server server) {
			return server;
		}
		throw new NullPointerException("The server instance is not yet available because the server has not yet been initialized");
	}
	
	@Override
	public void load(@NotNull String[] args) {
		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();
		OptionSpec<String> host = parser.accepts("host").withRequiredArg().ofType(String.class);
		OptionSpec<Integer> port = parser.accepts("port").withRequiredArg().ofType(Integer.class);
		OptionSet set = parser.parse(args);
		if (set.has(host)) {
			this.host = set.valueOf(host);
		} else {
			this.host = "localhost";
			LOGGER.warn("The attempt to get the host failed, the default host 127.0.0.1 is used instead");
		}
		if (set.has(port)) {
			this.port = set.valueOf(port);
		} else {
			this.port = 8081;
			LOGGER.warn("The attempt to get the port failed, the default port {} is used instead", this.port);
		}
	}
	
	@Override
	public void run() {
		this.networkInstance.get().open();
		LOGGER.info("Launch dedicated virtual game collection server on host {} with port {}", this.host, this.port);
	}
	
	@Override
	public @NotNull ApplicationType getType() {
		return ApplicationType.SERVER;
	}
	
	@Override
	public @NotNull NetworkInstance getNetworkInstance() {
		return this.networkInstance.get();
	}
	
	@Override
	public @NotNull ServerPlayerList getPlayerList() {
		return this.playerList;
	}
	
	@Override
	public @NotNull GameManager getGameManager() {
		return this.gameManager;
	}
	
	public @NotNull String getHost() {
		return this.host;
	}
	
	public int getPort() {
		return this.port;
	}
	
	@Override
	public void save() {
	
	}
}
