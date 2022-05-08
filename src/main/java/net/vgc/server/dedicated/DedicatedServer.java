package net.vgc.server.dedicated;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.common.collect.Lists;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.vgc.Constans;
import net.vgc.client.fx.FxUtil;
import net.vgc.data.tag.TagUtil;
import net.vgc.data.tag.tags.CompoundTag;
import net.vgc.game.Game;
import net.vgc.language.TranslationKey;
import net.vgc.network.Connection;
import net.vgc.network.NetworkSide;
import net.vgc.network.PacketDecoder;
import net.vgc.network.PacketEncoder;
import net.vgc.server.AbstractServer;
import net.vgc.server.network.ServerPacketListener;
import net.vgc.server.player.ServerPlayer;

public class DedicatedServer extends AbstractServer {
	
	protected final List<Channel> channels = Lists.newArrayList();
	protected final List<Connection> connections = Lists.newArrayList();
	protected TreeItem<String> playersTreeItem;
	protected Game game;
	
	public DedicatedServer(String host, int port, Path serverDirectory) throws Exception {
		super(host, port, serverDirectory);
		if (!Files.exists(serverDirectory)) {
			Files.createDirectories(serverDirectory.getParent());
			LOGGER.debug("Create server directory");
		}
	}

	@Override
	public boolean isDedicated() {
		return true;
	}
	
	@Override
	public void init() throws IOException {
		this.playerList = new DedicatedPlayerList(this);
		super.init();
	}

	@Override
	protected void load(CompoundTag tag) {
		if (tag.contains("admin")) {
			this.admin = TagUtil.readUUID(tag.getCompound("admin"));
		}
	}

	@Override
	public void displayServer(Stage stage) {
		stage.setScene(this.makeScene());
		stage.setTitle(TranslationKey.createAndGet("server.constans.name"));
		stage.setResizable(false);
		stage.show();
	}
	
	protected Scene makeScene() {
		VBox box = new VBox();
		TreeView<String> serverTree = new TreeView<>();
		TreeItem<String> treeItem = new TreeItem<String>(TranslationKey.createAndGet("server.window.server"));
		treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("server.window.server_host", this.host)));
		treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("server.window.server_port", this.port)));
		treeItem.getChildren().add(new TreeItem<String>(TranslationKey.createAndGet("server.window.server_admin", this.admin)));
		this.playersTreeItem = new TreeItem<String>(TranslationKey.createAndGet("server.window.players"));
		for (ServerPlayer player : this.playerList.getPlayers()) {
			this.playersTreeItem.getChildren().add(player.display());
		}
		treeItem.getChildren().add(this.playersTreeItem);
		serverTree.setRoot(treeItem);
		serverTree.setShowRoot(Constans.DEBUG);
		GridPane pane = FxUtil.makeGrid(Pos.CENTER, 5.0, 5.0);
		Button settingsButton = FxUtil.makeButton(TranslationKey.createAndGet("screen.menu.settings"), this::openSettings);
		settingsButton.setPrefWidth(150.0);
		Button refreshButton = FxUtil.makeButton(TranslationKey.createAndGet("account.window.refresh"), this::refreshPlayers);
		refreshButton.setPrefWidth(150.0);
		Button closeButton = FxUtil.makeButton(TranslationKey.createAndGet("account.window.close"), Platform::exit);
		closeButton.setPrefWidth(150.0);
		pane.addRow(0, settingsButton, refreshButton, closeButton);
		box.getChildren().addAll(serverTree, pane);
		return new Scene(box, 450.0, 400.0);
	}
	
	protected void openSettings() {
		LOGGER.debug("Settings");
	}
	
	public void refreshPlayers() {
		this.playersTreeItem.getChildren().removeIf((string) -> true);
		for (ServerPlayer player : this.playerList.getPlayers()) {
			this.playersTreeItem.getChildren().add(player.display());
		}
	}

	@Override
	public void startServer() {
		new ServerBootstrap().group(this.group).channel(NATIVE ? EpollServerSocketChannel.class : NioServerSocketChannel.class).childHandler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				ChannelPipeline pipeline = channel.pipeline();
				Connection connection = new Connection(new ServerPacketListener(DedicatedServer.this, NetworkSide.SERVER));
				pipeline.addLast("decoder", new PacketDecoder());
				pipeline.addLast("encoder", new PacketEncoder());
				pipeline.addLast("handler", connection);
				DedicatedServer.this.channels.add(channel);
				DedicatedServer.this.connections.add(connection);
				LOGGER.debug("Client connected with address {}", channel.remoteAddress().toString().replace("/", ""));
			}
		}).localAddress(this.host, this.port).bind().syncUninterruptibly().channel();
		LOGGER.info("Launch dedicated virtual game collection server on host {} with port {}", this.host, this.port);
	}
	
	@Override
	public void leavePlayer(Connection connection, ServerPlayer player) {
		if (this.channels.contains(connection.getChannel()) && this.connections.contains(connection)) {
			this.channels.remove(connection.getChannel());
			this.connections.remove(connection);
			LOGGER.debug("Remove channel and connection for player {}", player != null ? player.getProfile().getName() : "");
		}
		if (player != null) {
			super.leavePlayer(connection, player);
		}
	}
	
	public Game getGame() {
		return this.game;
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	@Override
	public void stopServer() throws Exception {
		super.stopServer();
		this.connections.clear();
		this.channels.forEach(Channel::close);
		this.channels.clear();
		this.group.shutdownGracefully();
	}

	@Override
	protected CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		if (this.admin != null)  {
			tag.putCompound("admin", TagUtil.writeUUID(this.admin));
		}
		return tag;
	}

}
