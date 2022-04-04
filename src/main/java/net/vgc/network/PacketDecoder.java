package net.vgc.network;

import java.io.IOException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.vgc.network.packet.Packet;
import net.vgc.network.packet.Packets;

public class PacketDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext context, ByteBuf input, List<Object> output) throws Exception {
		int i = input.readableBytes();
		if (i != 0) {
			FriendlyByteBuffer buffer = new FriendlyByteBuffer(input);
			int id = buffer.readInt();
			Packet<?> packet = Packets.getPacket(id, buffer);
			if (packet == null) {
				throw new IOException("Fail to get Packet for id: " + id);
			} else {
				int readableBytes = buffer.readableBytes();
				if (readableBytes > 0) {
					throw new IOException("Packet was to larger then expected, found " + readableBytes + " bytes extra whilst reading Packet: " + Packets.byId(id).getSimpleName() + " with id: " + id);
				} else {
					output.add(packet);
				}
			}
		}
	}

}
