package net.luis.client;

import javafx.stage.Stage;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.luis.client.account.AccountManager;
import net.luis.client.players.ClientPlayerList;
import net.luis.client.screen.MenuScreen;
import net.luis.game.GameManager;
import net.luis.game.application.Application;
import net.luis.game.application.ApplicationType;
import net.luis.game.application.FxApplication;
import net.luis.game.application.GameApplication;
import net.luis.game.resources.ResourceManager;
import net.luis.network.instance.ClientInstance;
import net.luis.network.instance.NetworkInstance;
import net.luis.network.packet.HandshakePacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

@Application(ApplicationType.CLIENT)
public class Client extends FxApplication implements GameApplication {
	
	private static final Logger LOGGER = LogManager.getLogger(Client.class);
	
	private final NetworkInstance networkInstance = new ClientInstance(HandshakePacket::new);
	private final AccountManager accountManager = new AccountManager();
	private final ClientPlayerList playerList = new ClientPlayerList(this);
	private final GameManager gameManager = new GameManager();
	private String accountHost;
	private int accountPort;
	
	public Client(ApplicationType type, ResourceManager resourceManager, Stage stage) {
		super(type, resourceManager, stage, new MenuScreen(), true);
	}
	
	public static @NotNull Client getInstance() {
		if (FxApplication.getInstance() instanceof Client client) {
			return client;
		}
		throw new NullPointerException("The client instance is not yet available because the client has not yet been initialized");
	}
	
	@Override
	public void load(@NotNull String[] args) {
		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();
		OptionSpec<String> accountHost = parser.accepts("accountHost").withRequiredArg().ofType(String.class);
		OptionSpec<Integer> accountPort = parser.accepts("accountPort").withRequiredArg().ofType(Integer.class);
		OptionSpec<Boolean> cachePasswordLocal = parser.accepts("cachePasswordLocal").withRequiredArg().ofType(Boolean.class);
		OptionSet set = parser.parse(args);
		if (set.has(accountHost)) {
			this.accountHost = set.valueOf(accountHost);
		} else {
			this.accountHost = "localhost";
			LOGGER.warn("The attempt to get the account server host failed, the default host 127.0.0.1 is used instead");
		}
		if (set.has(accountPort)) {
			this.accountPort = set.valueOf(accountPort);
		} else {
			this.accountPort = 8081;
			LOGGER.warn("The attempt to get the account server port failed, the default port {} is used instead", this.accountPort);
		}
		if (set.has(cachePasswordLocal)) {
			this.accountManager.setCachePasswordLocal(set.valueOf(cachePasswordLocal));
		} else {
			this.accountManager.setCachePasswordLocal(true);
			LOGGER.info("Password caching is not specified, use the default: true");
		}
	}
	
	@Override
	public void run() {
	
	}
	
	@Override
	public @NotNull ApplicationType getType() {
		return ApplicationType.CLIENT;
	}
	
	@Override
	public @NotNull NetworkInstance getNetworkInstance() {
		return this.networkInstance;
	}
	
	public @NotNull AccountManager getAccountManager() {
		return this.accountManager;
	}
	
	@Override
	public @NotNull ClientPlayerList getPlayerList() {
		return this.playerList;
	}
	
	@Override
	public @NotNull GameManager getGameManager() {
		return this.gameManager;
	}
	
	public @NotNull String getAccountHost() {
		return this.accountHost;
	}
	
	public int getAccountPort() {
		return this.accountPort;
	}
	
	@Override
	public void save() {
	
	}
}
