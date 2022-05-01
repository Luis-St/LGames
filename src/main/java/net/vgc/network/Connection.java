package net.vgc.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.vgc.network.packet.Packet;
import net.vgc.network.packet.PacketListener;

public class Connection extends SimpleChannelInboundHandler<Packet<?>> {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected final PacketListener listener;
	protected final List<Connection.PacketHolder> waitingPackets = new ArrayList<>();
	protected Channel channel;
	protected int sentPackets;
	protected int receivedPackets;
	protected int failedPackets;
	
	public Connection(PacketListener listener) {
		this.listener = listener;
		this.listener.setConnection(this);
	}
	
	@Override
	public void channelActive(ChannelHandlerContext context) throws Exception {
		this.channel = context.channel();
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext context, Packet<?> packet) throws Exception {
		if (this.channel.isOpen()) {
			try {
				LOGGER.debug("Received packet of type {}", packet.getClass().getSimpleName());
				this.handle(this.listener, packet);
				++this.receivedPackets;
			} catch (ClassCastException e) {
				LOGGER.warn("Fail to handle packet of type {}", packet.getClass().getSimpleName());
				++this.failedPackets;
			}
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
		if (cause instanceof SkipPacketException) {
			LOGGER.info("Skipping packet");
		} else if (this.channel.isOpen()) {
			LOGGER.warn("Caught Exception", cause);
			if (cause instanceof TimeoutException) {
				throw new IOException("Timeout", cause);
			} else {
				throw new IOException("Internal exception", cause);
			}
		} else {
			LOGGER.warn("Caught exception while channel is closed", cause);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends PacketListener> void handle(PacketListener listener, Packet<T> packet) {
		packet.handleLater((T) listener);
	}
	
	public Channel getChannel() {
		return this.channel;
	}
	
	public PacketListener getListener() {
		return this.listener;
	}
	
	public boolean isConnected() {
		return this.channel != null && this.channel.isOpen();
	}
	
	public void send(Packet<?> packet) {
		this.send(packet, null);
	}
	
	public void send(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> listener) {
		if (this.isConnected()) {
			this.flush();
			this.sendPacket(packet, listener, false);
		} else {
			LOGGER.info("Unable to send packet, since the connection is not open");
			this.waitingPackets.add(new Connection.PacketHolder(packet, listener));
		}
	}
	
	protected void sendPacket(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> listener, boolean waiting) {
		ChannelFuture channelFuture = this.channel.writeAndFlush(packet);
		LOGGER.debug("Send{}packet of type {}", waiting ? " waiting " : " ", packet.getClass().getSimpleName());
		if (listener != null) {
			channelFuture.addListener(listener);
		}
		channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
		++this.sentPackets;
	}
	
	protected void flush() {
		if (this.isConnected()) {
			Iterator<PacketHolder> iterator = this.waitingPackets.iterator();
			while (iterator.hasNext()) {
				PacketHolder packetHolder = iterator.next();
				this.sendPacket(packetHolder.packet(), packetHolder.listener(), true);
				iterator.remove();
			}
		}
	}
	
	public void close() {
		if (this.channel != null) {
			this.channel.close();
			this.channel = null;
		}
	}
	
	public boolean isClosed() {
		return this.channel == null;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Connection connection) {
			if (this.sentPackets != connection.sentPackets) {
				return false;
			} else if (this.receivedPackets != connection.receivedPackets) {
				return false;
			} else if (this.failedPackets != connection.failedPackets) {
				return false;
			} else if (!this.listener.equals(connection.listener)) {
				return false;
			} else {
				return this.channel.equals(connection.channel);
			}
		}
		return false;
	}
	
	protected static record PacketHolder(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> listener) {
		
	}
	
}
