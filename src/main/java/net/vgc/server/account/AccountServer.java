package net.vgc.server.account;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

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
import io.netty.handler.timeout.ReadTimeoutHandler;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.vgc.Constans;
import net.vgc.client.window.ErrorWindow;
import net.vgc.common.ErrorLevel;
import net.vgc.common.LaunchState;
import net.vgc.common.application.GameApplication;
import net.vgc.data.serialization.SerializationUtil;
import net.vgc.data.tag.Tag;
import net.vgc.data.tag.tags.CompoundTag;
import net.vgc.data.tag.tags.collection.ListTag;
import net.vgc.network.Connection;
import net.vgc.network.PacketDecoder;
import net.vgc.network.PacketEncoder;
import net.vgc.network.packet.PacketListener;
import net.vgc.server.account.network.AccountServerPacketListener;

public class AccountServer extends GameApplication {
	
	public static AccountServer instance;
	
	@Nullable
	public static AccountServer getInstance() {
		return instance;
	}
	
	protected final EventLoopGroup group = NATIVE ? new EpollEventLoopGroup() : new NioEventLoopGroup();
	protected final PacketListener listener = new AccountServerPacketListener();
	protected final List<Connection> connections = Lists.newArrayList();
	protected final List<Channel> channels = Lists.newArrayList();
	protected Random rng;
	protected String accountServerHost;
	protected int accountServerPort;
	protected Path accountServerDirectory;
	protected LaunchState launchState = LaunchState.UNKNOWN;
	protected AccountAgent agent;
	
	@Override
	public void init() throws Exception {
		super.init();
		LOGGER.info("Initial Account Server");
		instance = this;
		this.rng = new Random(System.currentTimeMillis());
	}
	
	@Override
	public void start(String[] args) throws Exception { // TODO: create Scene
		LOGGER.info("Starting Account Server");
		this.launchState = LaunchState.STARTING;
		this.stage.setScene(new Scene(new Group(), 400, 400));
		this.stage.show();
		this.handleStart(args);
		this.launchServer();
		this.loadAccounts();
		this.launchState = LaunchState.STARTED;
		LOGGER.info("Successfully start of Account Server with version {}", Constans.Account.VERSION);
	}
	
	protected void handleStart(String[] args) throws Exception {
		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();
		OptionSpec<String> accountServerHost = parser.accepts("accountServerHost").withRequiredArg().ofType(String.class);
		OptionSpec<Integer> accountServerPort = parser.accepts("accountServerPort").withRequiredArg().ofType(Integer.class);
		OptionSpec<File> accountServerDir = parser.accepts("accountServerDir").withRequiredArg().ofType(File.class);
		OptionSet set = parser.parse(args);
		if (set.has(accountServerHost)) {
			this.accountServerHost = set.valueOf(accountServerHost);
		} else {
			LOGGER.warn("Fail to get Account Server host, use default host: 127.0.0.1");
		}
		if (set.has(accountServerPort)) {
			this.accountServerPort = set.valueOf(accountServerPort);
		} else {
			LOGGER.warn("Fail to get Account Server port, use default port: 8081");
		}
		if (set.has(accountServerDir)) {
			this.accountServerDirectory = set.valueOf(accountServerDir).toPath();
		} else {
			LOGGER.warn("Fail to get account directory");
			ErrorWindow.make("Fail to get account directory", () -> { // TODO: interrupt launch til account directory is set
				DirectoryChooser chooser = new DirectoryChooser();
				chooser.setTitle("Choose account directory");
				this.accountServerDirectory = chooser.showDialog(new Stage()).toPath();
				LOGGER.info("Set account directory to {}", this.accountServerDirectory);
			}).setErrorLevel(ErrorLevel.ERROR).show();
		}
	}
	
	protected void launchServer() {
		new ServerBootstrap().group(this.group).channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class).childHandler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				ChannelPipeline pipeline = channel.pipeline();
				Connection connection = new Connection(AccountServer.this.listener);
				pipeline.addLast("timeout", new ReadTimeoutHandler(120));
				pipeline.addLast("decoder", new PacketDecoder());
				pipeline.addLast("encoder", new PacketEncoder());
				pipeline.addLast("handler", connection);
				AccountServer.this.channels.add(channel);
				AccountServer.this.connections.add(connection);
				LOGGER.debug("Client connecte with address {}", channel.remoteAddress());
			}
		}).localAddress(this.accountServerHost, this.accountServerPort).bind().syncUninterruptibly().channel();
		LOGGER.info("Launch Account Server on host {} with port {}", this.accountServerHost, this.accountServerPort);
	}
	
	protected void loadAccounts() throws Exception {
		List<PlayerAccount> accounts = Lists.newArrayList();
		Path path = this.accountServerDirectory.resolve("accounts.acc");
		LOGGER.info("Loading accounts from {}");
		if (!Files.exists(path)) {
			LOGGER.info("No accounts present");
		} else {
			Tag tag = Tag.load(path);
			if (tag instanceof CompoundTag loadTag) {
				if (loadTag.contains("accounts", Tag.LIST_TAG)) {
					ListTag accountsTag = loadTag.getList("accounts", Tag.LIST_TAG);
					for (Tag accountTag : accountsTag) {
						if (accountTag instanceof CompoundTag) {
							PlayerAccount account = SerializationUtil.deserialize(PlayerAccount.class, (CompoundTag) tag);
							if (account != null) {
								LOGGER.debug("Load {}", account);
								accounts.add(account);
							} else {
								LOGGER.error("Fail to load PlayerAccount");
								throw new NullPointerException("Something went wrong while loading PlayerAccount, since \"account\" is null");
							}
						} else {
							LOGGER.warn("Fail to load PlayerAccount, since Tag {} is not an instance of CompoundTag, but it is a type of {}", accountsTag, accountTag.getClass().getSimpleName());
						}
					}
				} else {
					LOGGER.warn("Fail to load PlayerAccounts from File {}, since the CompoundTag {} does not contains the key \"accounts\"", path, loadTag);
				}
			} else {
				LOGGER.warn("Fail to load PlayerAccounts from File {}, since Tag {} is not an instance of CompoundTag, but it is a type of {}", path, tag, tag.getClass().getSimpleName());	
			}
		}
		LOGGER.debug("Load {} PlayerAccounts", accounts.size());
		this.agent = new AccountAgent(accounts);
	}
	
	@Override
	protected String getThreadName() {
		return "account-server";
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
		LOGGER.info("Stopping Account Server");
		this.launchState = LaunchState.STOPPING;
		this.saveAccounts();
		this.stopServer();
		this.launchState = LaunchState.STOPPED;
		LOGGER.info("Successfully stopping Account Server");
	}
	
	protected void saveAccounts() throws Exception {
		Path path = this.accountServerDirectory.resolve("accounts.acc");
		LOGGER.debug("Remove guest PlayerAccounts");
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
		tag.put("accounts", accountsTag);
		Tag.save(path, accountsTag);
		LOGGER.info("Save {} PlayerAccounts", accountsTag.size());
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
