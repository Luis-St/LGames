package net.luis.network.instance;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.luis.network.Connection;
import net.luis.network.SimpleChannelInitializer;
import net.luis.network.packet.Packet;
import net.luis.utils.util.DefaultExceptionHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author Luis-st
 *
 */

public class ClientInstance implements NetworkInstance {
	
	private final Function<Channel, Connection> factory;
	private final Supplier<Packet> handshake;
	private EventLoopGroup group;
	
	public ClientInstance(@NotNull Supplier<Packet> handshake) {
		this(Connection::new, handshake);
	}
	
	public ClientInstance(@NotNull Function<Channel, Connection> factory, @NotNull Supplier<Packet> handshake) {
		this.factory = factory;
		this.handshake = handshake;
	}
	
	@Override
	public void open(@NotNull String host, int port) {
		Class<? extends Channel> channelClass = NATIVE ? EpollSocketChannel.class : NioSocketChannel.class;
		try {
			Channel channel = new Bootstrap().group(this.buildGroup()).channel(channelClass).handler(new SimpleChannelInitializer(factory)).connect(host, port).syncUninterruptibly().channel();
			ChannelFuture channelFuture = channel.writeAndFlush(this.handshake.get());
			channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
		} catch (Exception e) {
			throw new RuntimeException("Fail to start client connection", e);
		}
	}
	
	@Override
	public boolean isOpen() {
		return this.group != null;
	}
	
	private @NotNull EventLoopGroup buildGroup() {
		ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("client network").setUncaughtExceptionHandler(new DefaultExceptionHandler()).build();
		this.group = NATIVE ? new EpollEventLoopGroup(0, threadFactory) : new NioEventLoopGroup(0, threadFactory);
		return Objects.requireNonNull(this.group);
	}
	
	@Override
	public void close() {
		this.group.shutdownGracefully();
		this.group = null;
	}
	
}
