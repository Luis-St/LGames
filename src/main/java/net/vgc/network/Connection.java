package net.vgc.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
				this.handle(this.listener, packet);
				++this.receivedPackets;
			} catch (ClassCastException e) {
				++this.failedPackets;
			}
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
		if (cause instanceof SkipPacketException) {
			
		} else if (this.channel.isOpen()) {
			if (cause instanceof TimeoutException) {
				this.disconnect("Timeout", cause);
			} else {
				this.disconnect("Internal Exception", cause);
			}
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
			this.sendPacket(packet, listener);
		} else {
			this.waitingPackets.add(new Connection.PacketHolder(packet, listener));
		}
	}
	
	protected void sendPacket(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> listener) {
		ChannelFuture channelFuture = this.channel.writeAndFlush(packet);
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
				this.sendPacket(packetHolder.packet(), packetHolder.listener());
				iterator.remove();
			}
		}
	}
	
	public void disconnect(String message, Throwable cause) throws IOException {
		throw new IOException(message, cause);
	}
	
	protected static record PacketHolder(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> listener) {
		
	}
	
}
