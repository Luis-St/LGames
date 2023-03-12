package net.luis.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.exception.SkipPacketException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class PacketDecoder extends ByteToMessageDecoder {
	
	private static final Logger LOGGER = LogManager.getLogger(PacketDecoder.class);
	
	@Override
	protected void decode(@NotNull ChannelHandlerContext context, @NotNull ByteBuf input, @NotNull List<Object> output) throws Exception {
		int i = input.readableBytes();
		if (i != 0) {
			FriendlyByteBuffer buffer = new FriendlyByteBuffer(input);
			int id = buffer.readInt();
			Packet packet = Packets.getPacket(id, buffer);
			if (packet == null) {
				LOGGER.error("Failed to get packet for id {}", id);
				throw new IOException("Failed to get packet for id " + id);
			} else {
				int readableBytes = buffer.readableBytes();
				if (readableBytes > 0) {
					try {
						output.add(packet);
						LOGGER.warn("Handle packet of type {} carefully, as there may be a decoding problem", packet.getClass().getSimpleName());
					} catch (Exception e) {
						if (packet.skippable()) {
							throw new SkipPacketException();
						} else {
							LOGGER.error("Packet was too big than expected, found {} extra bytes while reading packet {} with id {}", readableBytes, packet.getClass().getSimpleName(), id);
							LOGGER.error("Catching an error while sensitively handling a packet with too many bytes");
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
	public void exceptionCaught(@NotNull ChannelHandlerContext context, @NotNull Throwable cause) {
		LOGGER.error("Caught an exception while decoding a packet");
	}
	
}
