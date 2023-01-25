package net.luis.account;

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
import net.luis.Constants;
import net.luis.account.account.Account;
import net.luis.account.account.AccountManager;
import net.luis.account.account.AccountType;
import net.luis.account.network.AccountPacketHandler;
import net.luis.account.window.AccountCreationWindow;
import net.luis.fxutils.FxUtils;
import net.luis.fxutils.PropertyListeners;
import net.luis.game.application.ApplicationType;
import net.luis.game.application.GameApplication;
import net.luis.language.Language;
import net.luis.language.LanguageProvider;
import net.luis.language.Languages;
import net.luis.language.TranslationKey;
import net.luis.network.Connection;
import net.luis.network.packet.PacketDecoder;
import net.luis.network.packet.PacketEncoder;
import net.luis.utility.ExceptionHandler;
import net.luis.utils.data.serialization.SerializationUtils;
import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.tags.CompoundTag;
import net.luis.utils.data.tag.tags.collection.ListTag;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class AccountServer extends GameApplication {
	
	private final EventLoopGroup group = GameApplication.NATIVE ? new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("connection #%d").setUncaughtExceptionHandler(new ExceptionHandler()).build())
			: new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("connection #%d").setUncaughtExceptionHandler(new ExceptionHandler()).build());
	private final List<Connection> connections = Lists.newArrayList();
	private final List<Channel> channels = Lists.newArrayList();
	private final AccountPacketHandler packetHandler = new AccountPacketHandler(this);
	private TreeView<String> accountView;
	private String host;
	private int port;
	private AccountManager manager;
	
	public static AccountServer getInstance() {
		if (GameApplication.getInstance() instanceof AccountServer account) {
			return account;
		}
		throw new NullPointerException("The account server instance is not yet available because the account server has not yet been initialized");
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
		OptionSet set = parser.parse(args);
		if (set.has(gameDir)) {
			this.gameDirectory = set.valueOf(gameDir).toPath();
		} else {
			this.gameDirectory = new File(System.getProperty("user.home")).toPath().resolve("Desktop/run/account_server");
			GameApplication.LOGGER.warn("Fail to get game directory, use default directory: {}", this.gameDirectory);
		}
		if (!Files.exists(this.gameDirectory)) {
			Files.createDirectories(this.gameDirectory);
			GameApplication.LOGGER.debug("Create account server directory");
			
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
		if (set.has(host)) {
			this.host = set.valueOf(host);
		} else {
			this.host = "localhost";
			GameApplication.LOGGER.warn("Fail to get host, use default host: 127.0.0.1");
		}
		if (set.has(port)) {
			this.port = set.valueOf(port);
		} else {
			this.port = 8081;
			GameApplication.LOGGER.warn("Fail to get port, use default port: 8081");
		}
		if (set.has(language)) {
			Language lang = Languages.fromFileName(set.valueOf(language));
			if (lang != null) {
				LanguageProvider.INSTANCE.setCurrentLanguage(lang);
			} else {
				GameApplication.LOGGER.info("Fail to get language, since the {} language does not exists or is not load", set.valueOf(language));
			}
		}
		this.launchServer();
	}
	
	@Override
	public void load() {
		List<Account> accounts = Lists.newArrayList();
		Path path = this.gameDirectory.resolve("accounts.acc");
		GameApplication.LOGGER.debug("Loading accounts from {}", path);
		if (!Files.exists(path)) {
			GameApplication.LOGGER.info("No accounts present, since file does not exists");
		} else {
			Tag tag = Tag.load(path);
			if (tag instanceof CompoundTag loadTag) {
				if (loadTag.contains("Accounts", Tag.LIST_TAG)) {
					ListTag accountsTag = loadTag.getList("Accounts", Tag.COMPOUND_TAG);
					if (accountsTag.isEmpty()) {
						GameApplication.LOGGER.info("No accounts present");
					} else {
						for (Tag accountTag : accountsTag) {
							if (accountTag instanceof CompoundTag) {
								Account account = SerializationUtils.deserialize(Account.class, (CompoundTag) accountTag);
								GameApplication.LOGGER.debug("Load {} account", account);
								accounts.add(account);
							} else {
								GameApplication.LOGGER.warn("Fail to load account, since tag {} is not an instance of CompoundTag, but it is a type of {}", accountsTag, accountTag.getClass().getSimpleName());
							}
						}
					}
				} else {
					if (loadTag.isEmpty()) {
						GameApplication.LOGGER.info("No accounts present in {}", path);
					} else {
						GameApplication.LOGGER.warn("Fail to load player accounts from file {}, since the CompoundTag {} does not contains the key \"accounts\"", path, loadTag);
					}
				}
			} else {
				GameApplication.LOGGER.warn("Fail to load player accounts from file {}, since tag {} is not an instance of CompoundTag, but it is a type of {}", path, tag, tag.getClass().getSimpleName());
			}
		}
		GameApplication.LOGGER.debug("Load {} accounts", accounts.size());
		this.manager = new AccountManager(accounts);
	}
	
	private void launchServer() {
		new ServerBootstrap().group(this.group).channel(GameApplication.NATIVE ? EpollServerSocketChannel.class : NioServerSocketChannel.class).childHandler(new ChannelInitializer<>() {
			@Override
			protected void initChannel(Channel channel) {
				ChannelPipeline pipeline = channel.pipeline();
				Connection connection = new Connection();
				pipeline.addLast("splitter", new ProtobufVarint32FrameDecoder());
				pipeline.addLast("decoder", new PacketDecoder());
				pipeline.addLast("prepender", new ProtobufVarint32LengthFieldPrepender());
				pipeline.addLast("encoder", new PacketEncoder());
				pipeline.addLast("handler", connection);
				AccountServer.this.channels.add(channel);
				AccountServer.this.connections.add(connection);
				GameApplication.LOGGER.debug("Client connected with address {}", channel.remoteAddress().toString().replace("/", ""));
			}
		}).localAddress(this.host, this.port).bind().syncUninterruptibly().channel();
		GameApplication.LOGGER.info("Launch account server on host {} with port {}", this.host, this.port);
	}
	
	@Override
	protected void setupStage() {
		this.stage.setResizable(false);
		this.stage.setTitle(TranslationKey.createAndGet("account.constans.name"));
		this.stage.setScene(this.makeScene());
		this.stage.show();
	}
	
	private Scene makeScene() {
		VBox box = new VBox();
		this.accountView = new TreeView<>();
		this.accountView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		TreeItem<String> treeItem = new TreeItem<>(TranslationKey.createAndGet("account.window.accounts"));
		for (Account account : this.manager.accounts()) {
			treeItem.getChildren().add(account.display());
		}
		this.accountView.setRoot(treeItem);
		this.accountView.setShowRoot(Constants.DEBUG);
		GridPane pane = FxUtils.makeGrid(Pos.CENTER, 5.0, 5.0);
		Button createAccountButton = FxUtils.makeButton(TranslationKey.createAndGet("account.window.create"), this::createAccount);
		createAccountButton.setPrefWidth(110.0);
		Button removeAccountButton = FxUtils.makeButton(TranslationKey.createAndGet("account.window.remove"), this::removeAccount);
		removeAccountButton.setDisable(true);
		removeAccountButton.setPrefWidth(110.0);
		Button refreshButton = FxUtils.makeButton(TranslationKey.createAndGet("account.window.refresh"), this::refreshScene);
		refreshButton.setPrefWidth(110.0);
		Button closeButton = FxUtils.makeButton(TranslationKey.createAndGet("account.window.close"), this::exit);
		closeButton.setPrefWidth(110.0);
		pane.addRow(0, createAccountButton, removeAccountButton, refreshButton, closeButton);
		this.accountView.getSelectionModel().selectedItemProperty().addListener(PropertyListeners.create((oldValue, newValue) -> {
			if (newValue == null) {
				removeAccountButton.setDisable(true);
			} else if (this.accountView.getRoot() != newValue.getParent()) {
				removeAccountButton.setDisable(true);
			} else {
				Account account = this.manager.accounts().get(this.accountView.getRoot().getChildren().indexOf(newValue));
				removeAccountButton.setDisable(account.getType() == AccountType.TEST || account.isTaken());
			}
		}));
		box.getChildren().addAll(this.accountView, pane);
		return new Scene(box, 450.0, 400.0);
	}
	
	private void createAccount() {
		AccountCreationWindow window = new AccountCreationWindow(this, new Stage());
		window.show();
	}
	
	private void removeAccount() {
		TreeItem<String> selectedItem = this.accountView.getSelectionModel().getSelectedItem();
		if (this.accountView.getRoot() == selectedItem.getParent()) {
			Account account = this.manager.accounts().get(this.accountView.getRoot().getChildren().indexOf(selectedItem));
			if (account.getType() == AccountType.TEST) {
				GameApplication.LOGGER.warn("Can not remove a test account");
			} else {
				this.manager.removeAccount(account.getName().hashCode(), account.getPasswordHash());
				this.refreshScene();
			}
		}
	}
	
	public void refreshScene() {
		TreeItem<String> treeItem = new TreeItem<>();
		for (Account account : this.manager.accounts()) {
			treeItem.getChildren().add(account.display());
		}
		this.accountView.setRoot(treeItem);
		this.accountView.setShowRoot(Constants.DEBUG);
	}
	
	@Override
	public ApplicationType getApplicationType() {
		return ApplicationType.ACCOUNT;
	}
	
	public AccountPacketHandler getPacketHandler() {
		return this.packetHandler;
	}
	
	public Path getGameDirectory() {
		return this.gameDirectory;
	}
	
	public Path getResourceDirectory() {
		return this.resourceDirectory;
	}
	
	public AccountManager getManager() {
		return this.manager;
	}
	
	public void exitClient(Connection connection) {
		String address = connection.getChannel().remoteAddress().toString().replace("/", "");
		if (this.channels.contains(connection.getChannel()) && this.connections.contains(connection)) {
			this.channels.remove(connection.getChannel());
			this.connections.remove(connection);
			GameApplication.LOGGER.debug("Remove channel and connection for client {}", address);
		}
		GameApplication.LOGGER.debug("Client disconnected with adress {}", address);
	}
	
	@Override
	public void save() {
		Path path = this.gameDirectory.resolve("accounts.acc");
		GameApplication.LOGGER.debug("Remove guest accounts");
		List<Account> accounts = this.manager.accounts();
		accounts.removeIf((account) -> {
			return account.getType() == AccountType.GUEST || account.getType() == AccountType.TEST || account.getType() == AccountType.UNKNOWN;
		});
		CompoundTag tag = new CompoundTag();
		ListTag accountsTag = new ListTag();
		for (Account account : accounts) {
			if (account != null) {
				accountsTag.add(account.serialize());
			} else {
				GameApplication.LOGGER.warn("Fail to save player account, since it was null");
			}
		}
		if (!accountsTag.isEmpty()) {
			tag.put("Accounts", accountsTag);
		}
		Tag.save(path, tag);
		GameApplication.LOGGER.info("Save {} accounts", accountsTag.size());
	}
	
	@Override
	protected void handleStop() {
		this.manager.close();
		this.connections.clear();
		this.channels.forEach(Channel::close);
		this.channels.clear();
		this.group.shutdownGracefully();
	}
	
}
