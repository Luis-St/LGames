package net.vgc.network;

import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.vgc.network.packet.Packet;
import net.vgc.network.packet.PacketListener;

public class ConnectionHandler {
	
	protected static final boolean NATIVE = Epoll.isAvailable();
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected final EventLoopGroup group = NATIVE ? new EpollEventLoopGroup() : new NioEventLoopGroup();
	protected final String connectTo;
	protected final Connection connection;
	protected final Consumer<Connection> closeAction;
	protected Bootstrap bootstrap;
	protected Channel channel;

	
	public ConnectionHandler(String connectTo, PacketListener listener, Consumer<Connection> closeAction) {
		this.connectTo = connectTo;
		this.closeAction = closeAction;
		this.connection = new Connection(listener);
	}
	
	public void start() {
		this.bootstrap = new Bootstrap().group(this.group).channel(NATIVE ? EpollSocketChannel.class : NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				ChannelPipeline pipeline = channel.pipeline();
				pipeline.addLast("decoder", new PacketDecoder());
				pipeline.addLast("encoder", new PacketEncoder());
				pipeline.addLast("handler", ConnectionHandler.this.connection);
			}
		});
	}
	
	public boolean isStarted() {
		return this.bootstrap != null;
	}
	
	public void connect(String host, int port) {
		if (!this.isStarted()) {
			this.start();
		}
		try {
			this.channel = this.bootstrap.connect(host, port).sync().channel();
			LOGGER.info("Start connection to {} on host {} with port {}", this.connectTo, host, port);
		} catch (Exception e) {
			LOGGER.error("Fail to start connection to {} on host {} with port {}", this.connectTo, host, port);
			throw new RuntimeException(e);
		}
	}
	
	public void send(Packet<?> packet) {
		this.connection.send(packet);
	}
	
	public Channel getChannel() {
		return this.channel;
	}
	
	public Connection getConnection() {
		return this.connection;
	}
	
	public boolean isConnected() {
		return this.connection != null && this.connection.isConnected();
	}
	
	public void close() {
		try {
			if (this.connection.isConnected()) {
				this.closeAction.accept(this.connection);
			}
			if (this.connection != null) {
				this.connection.close();
			}
			if (this.channel != null) {
				this.channel.closeFuture();
			}
			LOGGER.info("Close connection to {}", this.connectTo);
		} catch (Exception e) {
			LOGGER.error("Fail to close connection to {}", this.connectTo);
			throw new RuntimeException(e);
		}
	}
	
	public boolean isClosed() {
		return !this.channel.isOpen() && this.connection.isClosed();
	}
	
	public void disconnect() {
		try {
			this.close();
			if (this.group != null) {
				this.group.shutdownGracefully();
			}
			LOGGER.info("Disconnect from {}", this.connectTo);
		} catch (Exception e) {
			LOGGER.error("Fail to disconnect from {}", this.connectTo);
			throw new RuntimeException(e);
		}
	}
	
	public boolean isDisconnected() {
		return this.group.isShutdown() && this.isClosed();
	}
	
}
