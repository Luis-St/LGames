package net.vgc.network;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.vgc.network.packet.Packet;
import net.vgc.network.packet.Packets;

public class PacketDecoder extends ByteToMessageDecoder {

	protected static final Logger LOGGER = LogManager.getLogger();
	
	@Override
	protected void decode(ChannelHandlerContext context, ByteBuf input, List<Object> output) throws Exception {
		int i = input.readableBytes();
		if (i != 0) {
			FriendlyByteBuffer buffer = new FriendlyByteBuffer(input);
			int id = buffer.readInt();
			Packet<?> packet = Packets.getPacket(id, buffer);
			if (packet == null) {
				LOGGER.error("Fail to get packet for id {}" + id);
				throw new IOException("Fail to get packet for id: " + id);
			} else {
				int readableBytes = buffer.readableBytes();
				if (readableBytes > 0) {
					LOGGER.warn("Packet was to larger then expected, found {} extra bytes whilst reading packet {} with id: {}", readableBytes, Packets.byId(id).getSimpleName(), id);
//					LOGGER.error("Packet {} was to larger then expected", Packets.byId(id).getSimpleName());
//					throw new IOException("Packet was to larger then expected, found " + readableBytes + " extra bytes whilst reading Packet: " + Packets.byId(id).getSimpleName() + " with id: " + id);
				} else {
					output.add(packet);
				}
			}
		}
	}

}
