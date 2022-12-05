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
import net.vgc.network.packet.PacketHandler;
import net.vgc.util.exception.SkipPacketException;

/**
 *
 * @author Luis-st
 *
 */

public class Connection extends SimpleChannelInboundHandler<Packet<?>> {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final PacketHandler handler;
	private final List<Connection.PacketHolder> waitingPackets = new ArrayList<>();
	private Channel channel;
	private int sentPackets;
	private int receivedPackets;
	private int failedPackets;
	
	public Connection(PacketHandler handler) {
		this.handler = handler;
		this.handler.setConnection(this);
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
				this.handle(this.handler, packet);
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
	protected <T extends PacketHandler> void handle(PacketHandler handler, Packet<T> packet) {
		packet.handleLater((T) handler);
	}
	
	public Channel getChannel() {
		return this.channel;
	}
	
	public PacketHandler getHandler() {
		return this.handler;
	}
	
	public boolean isConnected() {
		return this.channel != null && this.channel.isOpen();
	}
	
	public void send(Packet<?> packet) {
		this.send(packet, null);
	}
	
	public void send(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> handler) {
		if (this.isConnected()) {
			this.flush();
			this.sendPacket(packet, handler, false);
		} else {
			LOGGER.info("Unable to send packet of type {}, since the connection is closed", packet.getClass().getSimpleName());
			this.waitingPackets.add(new Connection.PacketHolder(packet, handler));
		}
	}
	
	private void sendPacket(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> handler, boolean waiting) {
		ChannelFuture channelFuture = this.channel.writeAndFlush(packet);
		LOGGER.debug("Send{}packet of type {}", waiting ? " waiting " : " ", packet.getClass().getSimpleName());
		if (handler != null) {
			channelFuture.addListener(handler);
		}
		channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
		++this.sentPackets;
	}
	
	private void flush() {
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
			} else if (!this.handler.equals(connection.handler)) {
				return false;
			} else {
				return this.channel.equals(connection.channel);
			}
		}
		return false;
	}
	
	private static record PacketHolder(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> listener) {
		
	}
	
}
