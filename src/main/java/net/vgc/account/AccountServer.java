package net.vgc.account;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.google.common.collect.Lists;

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
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.vgc.Constans;
import net.vgc.account.network.AccountServerPacketListener;
import net.vgc.account.window.AccountCreationWindow;
import net.vgc.client.fx.FxUtil;
import net.vgc.common.LaunchState;
import net.vgc.common.application.GameApplication;
import net.vgc.data.serialization.SerializationUtil;
import net.vgc.data.tag.Tag;
import net.vgc.data.tag.tags.CompoundTag;
import net.vgc.data.tag.tags.collection.ListTag;
import net.vgc.language.Language;
import net.vgc.language.LanguageProvider;
import net.vgc.language.Languages;
import net.vgc.language.TranslationKey;
import net.vgc.network.Connection;
import net.vgc.network.InvalidNetworkSideException;
import net.vgc.network.Network;
import net.vgc.network.NetworkSide;
import net.vgc.network.PacketDecoder;
import net.vgc.network.PacketEncoder;
import net.vgc.network.packet.PacketListener;

public class AccountServer extends GameApplication {
	
	public static AccountServer instance;
	
	public static AccountServer getInstance() {
		if (NetworkSide.ACCOUNT_SERVER.isOn()) {
			return instance;
		}
		throw new InvalidNetworkSideException(NetworkSide.ACCOUNT_SERVER);
	}
	
	protected final EventLoopGroup group = NATIVE ? new EpollEventLoopGroup() : new NioEventLoopGroup();
	protected final PacketListener listener = new AccountServerPacketListener(this, NetworkSide.ACCOUNT_SERVER);
	protected final List<Connection> connections = Lists.newArrayList();
	protected final List<Channel> channels = Lists.newArrayList();
	protected Random rng;

	protected Path gameDirectory;
	protected Path resourceDirectory;
	protected String host;
	protected int port;
	protected LaunchState launchState = LaunchState.UNKNOWN;
	protected AccountAgent agent;
	protected TreeView<String> accountView;
	
	@Override
	public void init() throws Exception {
		super.init();
		LOGGER.info("Initial account server");
		instance = this;
		this.rng = new Random(System.currentTimeMillis());
	}
	
	@Override
	public void start(String[] args) throws Exception {
		LOGGER.info("Starting account server");
		this.launchState = LaunchState.STARTING;
		Network.INSTANCE.setNetworkSide(NetworkSide.ACCOUNT_SERVER);
		this.handleStart(args);
		LanguageProvider.INSTANCE.load();
		this.launchServer();
		this.loadAccounts();
		this.stage.setResizable(false);
		this.stage.setTitle(TranslationKey.createAndGet("account.constans.name"));
		this.stage.setScene(this.makeScene());
		this.stage.show();
		this.launchState = LaunchState.STARTED;
		LOGGER.info("Successfully start of account server with version {}", Constans.Account.VERSION);
	}
	
	protected void handleStart(String[] args) throws Exception {
		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();
		OptionSpec<File> gameDir = parser.accepts("gameDir").withRequiredArg().ofType(File.class);
		OptionSpec<File> resourceDir = parser.accepts("resourceDir").withRequiredArg().ofType(File.class);
		OptionSpec<String> host = parser.accepts("host").withRequiredArg().ofType(String.class);
		OptionSpec<Integer> port = parser.accepts("port").withRequiredArg().ofType(Integer.class);
		OptionSpec<String> language = parser.accepts("language").withRequiredArg().ofType(String.class);
		OptionSet set = parser.parse(args);
		if (set.has(gameDir)) {
			this.gameDirectory = set.valueOf(gameDir).toPath();
		} else {
			this.gameDirectory = new File(System.getProperty("user.home")).toPath().resolve("Desktop/run/account_server");
			LOGGER.warn("Fail to get game directory, use default directory: {}", this.gameDirectory);
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
			LOGGER.debug("Create account server directory");
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
				LOGGER.info("Fail to get language, since the {} language does not exists", set.valueOf(language));
			}
		}
	}
	
	protected void launchServer() {
		new ServerBootstrap().group(this.group).channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class).childHandler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				ChannelPipeline pipeline = channel.pipeline();
				Connection connection = new Connection(AccountServer.this.listener);
				pipeline.addLast("decoder", new PacketDecoder());
				pipeline.addLast("encoder", new PacketEncoder());
				pipeline.addLast("handler", connection);
				AccountServer.this.channels.add(channel);
				AccountServer.this.connections.add(connection);
				LOGGER.debug("Client connected with address {}", channel.remoteAddress().toString().replace("/", ""));
			}
		}).localAddress(this.host, this.port).bind().syncUninterruptibly().channel();
		LOGGER.info("Launch account server on host {} with port {}", this.host, this.port);
	}
	
	protected void loadAccounts() {
		try {
			List<PlayerAccount> accounts = Lists.newArrayList();
			Path path = this.gameDirectory.resolve("accounts.acc");
			LOGGER.debug("Loading accounts from {}", path);
			if (!Files.exists(path)) {
				LOGGER.info("No accounts present");
			} else {
				Tag tag = Tag.load(path);
				if (tag instanceof CompoundTag loadTag) {
					if (loadTag.contains("accounts", Tag.LIST_TAG)) {
						ListTag accountsTag = loadTag.getList("accounts", Tag.COMPOUND_TAG);
						for (Tag accountTag : accountsTag) {
							if (accountTag instanceof CompoundTag) {
								PlayerAccount account = SerializationUtil.deserialize(PlayerAccount.class, (CompoundTag) accountTag);
								if (account != null) {
									LOGGER.debug("Load {}", account);
									accounts.add(account);
								} else {
									LOGGER.error("Fail to load PlayerAccount");
									throw new NullPointerException("Something went wrong while loading accounts, since \"account\" is null");
								}
							} else {
								LOGGER.warn("Fail to load account, since Tag {} is not an instance of CompoundTag, but it is a type of {}", accountsTag, accountTag.getClass().getSimpleName());
							}
						}
					} else {
						if (loadTag.isEmpty()) {
							LOGGER.info("No accounts present in {}", path);
						} else {
							LOGGER.warn("Fail to load accounts from File {}, since the CompoundTag {} does not contains the key \"accounts\"", path, loadTag);
						}
					}
				} else {
					LOGGER.warn("Fail to load accounts from File {}, since Tag {} is not an instance of CompoundTag, but it is a type of {}", path, tag, tag.getClass().getSimpleName());	
				}
			}
			LOGGER.debug("Load {} PlayerAccounts", accounts.size());
			this.agent = new AccountAgent(accounts);
		} catch (Exception e) {
			LOGGER.error("Fail to load accounts", e);
			throw new RuntimeException();
		}
	}
	
	protected Scene makeScene() {
		VBox viewBox = new VBox();
		this.accountView = new TreeView<>();
		this.accountView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		TreeItem<String> treeItem = new TreeItem<>(TranslationKey.createAndGet("account.window.accounts"));
		for (PlayerAccount account : this.agent.getAccounts()) {
			treeItem.getChildren().add(account.display());
		}
		this.accountView.setRoot(treeItem);
		this.accountView.setShowRoot(Constans.DEBUG);
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 5.0, 5.0);
		Button createAccountButton = FxUtil.makeButton(TranslationKey.createAndGet("account.window.create"), this::createAccount);
		createAccountButton.setPrefWidth(110.0);
		Button removeAccountButton = FxUtil.makeButton(TranslationKey.createAndGet("account.window.remove"), this::removeAccount);
		removeAccountButton.setPrefWidth(110.0);
		Button refreshButton = FxUtil.makeButton(TranslationKey.createAndGet("account.window.refresh"), this::refresh);
		refreshButton.setPrefWidth(110.0);
		Button closeButton = FxUtil.makeButton(TranslationKey.createAndGet("account.window.close"), this::exit);
		closeButton.setPrefWidth(110.0);
		pane.addRow(0, createAccountButton, removeAccountButton, refreshButton, closeButton);
		viewBox.getChildren().addAll(this.accountView, pane);
		return new Scene(viewBox, 450.0, 400.0);
	}
	
	protected void createAccount() {
		AccountCreationWindow window = new AccountCreationWindow(this, new Stage());
		window.show();
	}
	
	protected void removeAccount() {
		int index = this.accountView.getSelectionModel().getSelectedIndex();
		if (this.accountView.getRoot().getChildren().size() > index && index >= 0) {
			TreeItem<String> treeItem = this.accountView.getRoot().getChildren().get(index);
			if (treeItem.getChildren().size() == 4) {
				UUID uuid = UUID.fromString(treeItem.getChildren().get(2).getValue().split(": ")[1]);
				if (this.agent.removeAccount(uuid)) {
					this.accountView.getRoot().getChildren().remove(index);
					this.refresh();
				}
			}
		}
	}
	
	public void refresh() {
		TreeItem<String> treeItem = new TreeItem<>();
		for (PlayerAccount account : this.agent.getAccounts()) {
			treeItem.getChildren().add(account.display());
		}
		this.accountView.setRoot(treeItem);
		this.accountView.setShowRoot(Constans.DEBUG);
	}
	
	@Override
	protected String getThreadName() {
		return "account";
	}
	
	public Path getGameDirectory() {
		return this.gameDirectory;
	}
	
	public Path getResourceDirectory() {
		return this.resourceDirectory;
	}
	
	public AccountAgent getAgent() {
		return this.agent;
	}
	
	public void exit() {
		LOGGER.info("Exit Account Server");
		Platform.exit();
	}
	
	@Override
	public void stop() throws Exception {
		LOGGER.info("Stopping account server");
		this.launchState = LaunchState.STOPPING;
		this.saveAccounts();
		this.stopServer();
		this.launchState = LaunchState.STOPPED;
		LOGGER.info("Successfully stopping account server");
	}
	
	protected void saveAccounts() {
		try {
			Path path = this.gameDirectory.resolve("accounts.acc");
			LOGGER.debug("Remove guest accounts");
			List<PlayerAccount> accounts = this.getAgent().getAccounts();
			accounts.removeIf(PlayerAccount::isGuest);
			if (!Files.exists(path)) {
				Files.createDirectories(path.getParent());
				Files.createFile(path);
			}
			CompoundTag tag = new CompoundTag();
			ListTag accountsTag = new ListTag();
			for (PlayerAccount account : accounts) {
				if (account != null) {
					accountsTag.add(account.serialize());
				}
			}
			if (!accountsTag.isEmpty()) {
				tag.put("accounts", accountsTag);
			}
			Tag.save(path, tag);
			LOGGER.info("Save {} accounts", accountsTag.size());
		} catch (Exception e) {
			LOGGER.warn("Fail to save accounts", e);
		}
	}
	
	protected void stopServer() {
		if (this.launchState == LaunchState.STOPPING) {
			this.connections.clear();
			this.channels.forEach(Channel::close);
			this.channels.clear();
			this.group.shutdownGracefully();
		}
	}
	
	protected void handleStop() {
		this.agent.close();
	}
	
}
