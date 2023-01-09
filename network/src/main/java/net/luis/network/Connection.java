package net.luis.network;

import io.netty.channel.*;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.luis.network.exception.SkipPacketException;
import net.luis.network.packet.Packet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-st
 *
 */

public class Connection extends SimpleChannelInboundHandler<Packet> {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final List<Connection.PacketHolder> waitingPackets = new ArrayList<>();
	private Channel channel;
	private int sentPackets;
	private int receivedPackets;
	private int failedPackets;
	
	@Override
	public void channelActive(ChannelHandlerContext context) {
		this.channel = context.channel();
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext context, Packet packet) {
		if (this.channel.isOpen()) {
			try {
				LOGGER.debug("Received packet of type {}", packet.getClass().getSimpleName());
				packet.handleLater(this);
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
	
	public Channel getChannel() {
		return this.channel;
	}
	
	public boolean isConnected() {
		return this.channel != null && this.channel.isOpen();
	}
	
	public void send(Packet packet) {
		this.send(packet, null);
	}
	
	public void send(Packet packet, GenericFutureListener<? extends Future<? super Void>> handler) {
		if (this.isConnected()) {
			this.flush();
			this.sendPacket(packet, handler, false);
		} else {
			LOGGER.info("Unable to send packet of type {}, since the connection is closed", packet.getClass().getSimpleName());
			this.waitingPackets.add(new Connection.PacketHolder(packet, handler));
		}
	}
	
	private void sendPacket(Packet packet, GenericFutureListener<? extends Future<? super Void>> handler, boolean waiting) {
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Connection that)) return false;
		
		if (this.sentPackets != that.sentPackets) return false;
		if (this.receivedPackets != that.receivedPackets) return false;
		if (this.failedPackets != that.failedPackets) return false;
		if (!this.waitingPackets.equals(that.waitingPackets)) return false;
		return Objects.equals(this.channel, that.channel);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.waitingPackets, this.channel, this.sentPackets, this.receivedPackets, this.failedPackets);
	}
	
	private record PacketHolder(Packet packet, GenericFutureListener<? extends Future<? super Void>> listener) {
		
	}
	
}
