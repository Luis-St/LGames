package net.luis.network;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
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
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import net.luis.common.util.ExceptionHandler;
import net.luis.network.packet.Packet;
import net.luis.network.packet.PacketDecoder;
import net.luis.network.packet.PacketEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

/**
 *
 * @author Luis-st
 *
 */

public class ConnectionHandler {
	
	private static final boolean NATIVE = Epoll.isAvailable();
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final String connectTo;
	private final Consumer<Connection> closeAction;
	private EventLoopGroup group;
	private Channel channel;
	private Connection connection;
	
	public ConnectionHandler(String connectTo, Consumer<Connection> closeAction) {
		this.connectTo = connectTo;
		this.closeAction = closeAction;
	}
	
	public void connect(String host, int port) {
		try {
			ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("client network").setUncaughtExceptionHandler(new ExceptionHandler()).build();
			this.group = NATIVE ? new EpollEventLoopGroup(0, threadFactory) : new NioEventLoopGroup(0, threadFactory);
			this.connection = new Connection();
			this.channel = new Bootstrap().group(this.group).channel(NATIVE ? EpollSocketChannel.class : NioSocketChannel.class).handler(new ChannelInitializer<>() {
				@Override
				protected void initChannel(Channel channel) {
					ChannelPipeline pipeline = channel.pipeline();
					pipeline.addLast("splitter", new ProtobufVarint32FrameDecoder());
					pipeline.addLast("decoder", new PacketDecoder());
					pipeline.addLast("prepender", new ProtobufVarint32LengthFieldPrepender());
					pipeline.addLast("encoder", new PacketEncoder());
					pipeline.addLast("handler", ConnectionHandler.this.connection);
				}
			}).connect(host, port).sync().channel();
			LOGGER.info("Start connection to {} on host {} with port {}", this.connectTo, host, port);
		} catch (Exception e) {
			LOGGER.error("Fail to start connection to {} on host {} with port {}", this.connectTo, host, port);
			throw new RuntimeException(e);
		}
	}
	
	public void send(Packet packet) {
		if (this.isConnected()) {
			this.connection.send(packet);
		} else {
			LOGGER.warn("Fail to send packet to {} since the connection is closed", this.connectTo);
		}
	}
	
	@Nullable
	public Channel getChannel() {
		return this.channel;
	}
	
	@Nullable
	public Connection getConnection() {
		return this.connection;
	}
	
	public boolean isConnected() {
		return this.group != null && this.channel != null && this.connection != null && this.connection.isConnected();
	}
	
	public void close() {
		try {
			if (this.connection != null) {
				if (this.connection.isConnected()) {
					this.closeAction.accept(this.connection);
				}
				this.connection.close();
			}
			if (this.channel != null) {
				this.channel.closeFuture();
			}
			if (this.group != null) {
				this.group.shutdownGracefully();
			}
			this.connection = null;
			this.channel = null;
			this.group = null;
			LOGGER.info("Close connection to {}", this.connectTo);
		} catch (Exception e) {
			LOGGER.error("Fail to close connection to {}", this.connectTo);
			throw new RuntimeException(e);
		}
	}
	
	public boolean isClosed() {
		return this.group == null && this.channel == null && this.connection == null;
	}
	
}
