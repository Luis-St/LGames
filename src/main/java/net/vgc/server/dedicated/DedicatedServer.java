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
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.vgc.data.tag.TagUtil;
import net.vgc.data.tag.tags.CompoundTag;
import net.vgc.language.TranslationKey;
import net.vgc.network.Connection;
import net.vgc.network.NetworkSide;
import net.vgc.network.PacketDecoder;
import net.vgc.network.PacketEncoder;
import net.vgc.server.AbstractServer;
import net.vgc.server.network.ServerPacketListener;

public class DedicatedServer extends AbstractServer {
	
	protected final List<Connection> connections = Lists.newArrayList();
	protected final List<Channel> channels = Lists.newArrayList();
	
	public DedicatedServer(String host, int port, Path serverDirectory) throws Exception {
		super(host, port, serverDirectory);
		if (!Files.exists(serverDirectory)) {
			Files.createDirectories(serverDirectory.getParent());
		}
	}

	@Override
	public boolean isDedicated() {
		return true;
	}
	
	@Override
	public void init() throws IOException {
		this.playerList = new DedicatedPlayerList(this, this.serverDirectory.resolve("players.data"));
		super.init();
	}

	@Override
	protected void load(CompoundTag tag) {
		this.playerList.load();
		if (tag.contains("admin")) {
			this.admin = TagUtil.readUUID(tag.getCompound("admin"));
		}
	}

	@Override
	public void displayServer(Stage stage) {
		
		stage.setScene(new Scene(new Group(), 450.0, 450.0));
		
		stage.setTitle(TranslationKey.createAndGet("server.constans.name"));
		stage.setResizable(false);
		stage.show();
	}
	
	protected void refreshDisplay() {
		
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
	public void stopServer() {
		super.stopServer();
		this.connections.clear();
		this.channels.forEach((channel) ->  {
			channel.closeFuture().syncUninterruptibly();
		});
		this.channels.clear();
		this.group.shutdownGracefully();
	}

	@Override
	protected void save() {
		this.playerList.save();
	}

}
