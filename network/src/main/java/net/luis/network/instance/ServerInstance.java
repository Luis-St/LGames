package net.luis.network.instance;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.luis.network.SimpleChannelInitializer;
import net.luis.utils.util.DefaultExceptionHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;

/**
 *
 * @author Luis-st
 *
 */

public class ServerInstance implements NetworkInstance {
	
	private EventLoopGroup group;
	
	@Override
	public @NotNull Channel open(@NotNull String host, int port) {
		Class<? extends ServerChannel> channel = NATIVE ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
		try {
			return new ServerBootstrap().group(this.buildGroup()).channel(channel).childHandler(new SimpleChannelInitializer()).localAddress(host, port).bind().syncUninterruptibly().channel();
		} catch (Exception e) {
			throw new RuntimeException("Fail to start server", e);
		}
	}
	
	@Override
	public boolean isOpen() {
		return this.group != null;
	}
	
	private @NotNull EventLoopGroup buildGroup() {
		ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("connection #%d").setUncaughtExceptionHandler(new DefaultExceptionHandler()).build();
		this.group = NATIVE ? new EpollEventLoopGroup(0, threadFactory) : new NioEventLoopGroup(0, threadFactory);
		return Objects.requireNonNull(this.group);
	}
	
	@Override
	public void close() {
		this.group.shutdownGracefully();
		this.group = null;
	}
	
}
