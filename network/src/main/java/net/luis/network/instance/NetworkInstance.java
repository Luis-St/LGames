package net.luis.network.instance;

import io.netty.channel.Channel;
import io.netty.channel.epoll.Epoll;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public interface NetworkInstance {
	
	boolean NATIVE = Epoll.isAvailable();
	
	@NotNull Channel open(@NotNull String host, int port);
	
	boolean isOpen();
	
	void close();
	
}
