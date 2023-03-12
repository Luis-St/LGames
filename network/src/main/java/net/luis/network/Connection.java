package net.luis.network;

import io.netty.channel.*;
import io.netty.handler.timeout.TimeoutException;
import net.luis.network.exception.SkipPacketException;
import net.luis.network.packet.Packet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class Connection extends SimpleChannelInboundHandler<Packet> {
	
	private static final Logger LOGGER = LogManager.getLogger(Connection.class);
	
	private final UUID uuid = UUID.randomUUID();
	private final Channel channel;
	private int sentPackets;
	private int receivedPackets;
	private int failedPackets;
	
	public Connection(@NotNull Channel channel) {
		this.channel = channel;
	}
	
	public @NotNull UUID getUniqueId() {
		return this.uuid;
	}
	
	@Override
	protected void channelRead0(@NotNull ChannelHandlerContext context, @NotNull Packet packet) {
		if (this.channel.isOpen()) {
			try {
				LOGGER.debug("Received packet {}", packet.getClass().getSimpleName());
				packet.handleLater(this);
				++this.receivedPackets;
			} catch (ClassCastException e) {
				LOGGER.warn("Fail to handle packet {}", packet.getClass().getSimpleName());
				++this.failedPackets;
			}
		}
	}
	
	@Override
	public void exceptionCaught(@NotNull ChannelHandlerContext context, @NotNull Throwable cause) throws Exception {
		if (cause instanceof SkipPacketException) {
			LOGGER.info("Skipping packet");
		} else if (this.channel.isOpen()) {
			LOGGER.error("Caught Exception", cause);
			if (cause instanceof TimeoutException) {
				throw new IOException("Timeout", cause);
			} else {
				throw new IOException("Internal exception", cause);
			}
		} else {
			LOGGER.error("Caught exception while channel is closed", cause);
		}
	}
	
	public @NotNull Channel getChannel() {
		return this.channel;
	}
	
	public boolean isConnected() {
		return this.channel.isOpen();
	}
	
	public void send(@NotNull Packet packet) {
		if (this.isConnected()) {
			ChannelFuture channelFuture = this.channel.writeAndFlush(packet);
			LOGGER.debug("Send packet of type {}", packet.getClass().getSimpleName());
			channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
			++this.sentPackets;
		} else {
			throw new IllegalStateException("Unable to send packet because connection is closed");
		}
	}
	
	public void close() {
		this.channel.close();
	}
	
	public boolean isClosed() {
		return !this.channel.isOpen();
	}
	
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (!(o instanceof Connection that)) return false;
		
		if (this.sentPackets != that.sentPackets) return false;
		if (this.receivedPackets != that.receivedPackets) return false;
		if (this.failedPackets != that.failedPackets) return false;
		return Objects.equals(this.channel, that.channel);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.channel, this.sentPackets, this.receivedPackets, this.failedPackets);
	}
	
	@Override
	public @NotNull String toString() {
		return "Connection{uuid=" + this.uuid + ", channel=" + this.channel.remoteAddress().toString().replace("/", "") + "}";
	}
}
