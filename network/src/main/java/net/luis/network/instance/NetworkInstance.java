package net.luis.network.instance;

import io.netty.channel.epoll.Epoll;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-st
 *
 */

public interface NetworkInstance {
	
	boolean NATIVE = Epoll.isAvailable();
	
	void open(@NotNull String host, int port);
	
	boolean isOpen();
	
	void close();
	
}
