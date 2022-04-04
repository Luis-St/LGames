package net.vgc.network;

import io.netty.channel.epoll.Epoll;

public interface NetworkSystem extends NetworkRunnable {
	
	static boolean NATIVE = Epoll.isAvailable();
	
	void init() throws Exception;
	
	@Override
	void run() throws Exception;
	
	boolean stop() throws Exception;
	
}
