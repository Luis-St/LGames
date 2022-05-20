package net.vgc.network;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import net.vgc.network.packet.Packet;
import net.vgc.network.packet.PacketListener;
import net.vgc.util.ExceptionHandler;

public class ConnectionHandler {
	
	protected static final boolean NATIVE = Epoll.isAvailable();
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected final String connectTo;
	protected final Supplier<PacketListener> listenerFactory;
	protected final Consumer<Connection> closeAction;
	protected EventLoopGroup group;
	protected Channel channel;
	protected Connection connection;
	
	public ConnectionHandler(String connectTo, Supplier<PacketListener> listenerFactory, Consumer<Connection> closeAction) {
		this.connectTo = connectTo;
		this.listenerFactory = listenerFactory;
		this.closeAction = closeAction;
	}
	
	public void connect(String host, int port) {
		try {
			this.group = NATIVE ? new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("client network").setUncaughtExceptionHandler(new ExceptionHandler()).build())
				: new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("client network").setUncaughtExceptionHandler(new ExceptionHandler()).build());
			this.connection = new Connection(this.listenerFactory.get());
			this.channel = new Bootstrap().group(this.group).channel(NATIVE ? EpollSocketChannel.class : NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel channel) throws Exception {
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
	
	public void send(Packet<?> packet) {
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
