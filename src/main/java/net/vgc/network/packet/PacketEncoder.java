package net.vgc.network.packet;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.vgc.network.SkipPacketException;
import net.vgc.network.buffer.FriendlyByteBuffer;

public class PacketEncoder extends MessageToByteEncoder<Packet<?>> {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	@Override
	@SuppressWarnings("unchecked")
	protected void encode(ChannelHandlerContext context, Packet<?> packet, ByteBuf output) throws Exception {
		int id = Packets.getId((Class<? extends Packet<?>>) packet.getClass());
		if (id == -1) {
			LOGGER.error("Can not encode packet {}", packet.getClass().getSimpleName());
			throw new IOException("Can not encode packet " + packet.getClass().getSimpleName() + ", since it is not registered");
		} else {
			FriendlyByteBuffer buffer = new FriendlyByteBuffer(output);
			buffer.writeInt(id);
			try {
				int startSize = buffer.writerIndex();
				packet.encode(buffer);
				int size = buffer.writerIndex() - startSize;
				if (size > 8388608) {
					LOGGER.error("Packet {} is too big", packet.getClass().getSimpleName());
					throw new IllegalArgumentException("Packet " + packet.getClass().getSimpleName() + " is too big, it should be less than 8388608, but it is " + size);
				}
			} catch (Exception e) {
				if (packet.skippable()) {
					throw new SkipPacketException(e);
				} else {
					LOGGER.error("Fail to encode packet {} with id {}, since it is not skippable", packet.getClass().getSimpleName(), id);
					throw new RuntimeException("Fail to encode packet " + packet.getClass().getSimpleName(), e);
				}
			}

		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
		LOGGER.warn("Caught an exception while encode a packet");
	}

}
