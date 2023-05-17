package net.luis.account;

import javafx.stage.Stage;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.luis.account.account.AccountAgent;
import net.luis.game.application.Application;
import net.luis.game.application.ApplicationType;
import net.luis.game.application.FxApplication;
import net.luis.game.resources.ResourceManager;
import net.luis.netcore.network.NetworkInstance;
import net.luis.netcore.network.ServerInstance;
import net.luis.utils.data.serialization.SerializationUtils;
import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.tags.CompoundTag;
import net.luis.utils.util.LazyLoad;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

/**
 *
 * @author Luis-st
 *
 */

@Application(ApplicationType.ACCOUNT)
public class AccountServer extends FxApplication {
	
	private static final Logger LOGGER = LogManager.getLogger(AccountServer.class);
	
	private final LazyLoad<NetworkInstance> networkInstance = new LazyLoad<>(() -> {
		return new ServerInstance(this.host, this.port);
	});
	private final Supplier<AccountAgent> accountAgent = new LazyLoad<>(() -> {
		Path path = this.getResourceManager().gameDirectory().resolve("accounts.acc");
		LOGGER.info("Loading accounts from file {}", path);
		if (!Files.exists(path)) {
			LOGGER.info("No accounts loaded because no file was found to load accounts from");
			return new AccountAgent();
		} else {
			return SerializationUtils.deserialize(AccountAgent.class, (CompoundTag) Tag.load(path));
		}
	});
	private String host;
	private int port;
	
	public AccountServer(@NotNull ApplicationType type, @NotNull ResourceManager resourceManager, @NotNull Stage stage) {
		super(type, resourceManager, stage, new MainScreen(), false);
	}
	
	public static @NotNull AccountServer getInstance() {
		if (FxApplication.getInstance() instanceof AccountServer account) {
			return account;
		}
		throw new NullPointerException("The account server instance is not yet available because the account server has not yet been initialized");
	}
	
	@Override
	public void load(@NotNull String[] args) {
		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();
		OptionSpec<String> host = parser.accepts("host").withRequiredArg().ofType(String.class).defaultsTo("localhost");
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
		LOGGER.info("Launch account server on host {} with port {}", this.host, this.port);
	}
	
	@Override
	public @NotNull ApplicationType getType() {
		return ApplicationType.ACCOUNT;
	}
	
	@Override
	public @NotNull NetworkInstance getNetworkInstance() {
		return this.networkInstance.get();
	}
	
	public @NotNull AccountAgent getAccountAgent() {
		return this.accountAgent.get();
	}
	
	@Override
	public void save() {
		Path path = this.getResourceManager().gameDirectory().resolve("accounts.acc");
		LOGGER.info("Saving accounts to file {}", path);
		Tag.save(path, this.getAccountAgent().serialize());
		this.getAccountAgent().close();
	}
}
