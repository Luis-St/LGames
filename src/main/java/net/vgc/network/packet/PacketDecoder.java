package net.vgc.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.util.exception.SkipPacketException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class PacketDecoder extends ByteToMessageDecoder {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	@Override
	protected void decode(ChannelHandlerContext context, ByteBuf input, List<Object> output) throws Exception {
		int i = input.readableBytes();
		if (i != 0) {
			FriendlyByteBuffer buffer = new FriendlyByteBuffer(input);
			int id = buffer.readInt();
			Packet packet = Packets.getPacket(id, buffer);
			if (packet == null) {
				LOGGER.error("Fail to get packet for id {}", id);
				throw new IOException("Fail to get packet for id: " + id);
			} else {
				int readableBytes = buffer.readableBytes();
				if (readableBytes > 0) {
					try {
						output.add(packet);
						LOGGER.info("Handle packet of type {} sensitively, since there could be a decode issue", packet.getClass().getSimpleName());
					} catch (Exception e) {
						if (packet.skippable()) {
							throw new SkipPacketException();
						} else {
							LOGGER.warn("Packet was to larger then expected, found {} extra bytes whilst reading packet {} with id {}", readableBytes, packet.getClass().getSimpleName(), id);
							LOGGER.error("Catch an error while handle a packet sensitively with too much bytes");
							throw new RuntimeException(e);
						}
					}
				} else {
					output.add(packet);
				}
			}
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
		LOGGER.warn("Caught an exception while decode a packet");
	}
	
}
