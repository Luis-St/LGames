package net.vgc.network;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.vgc.network.packet.Packet;
import net.vgc.network.packet.Packets;

public class PacketEncoder extends MessageToByteEncoder<Packet<?>> {

	@Override
	@SuppressWarnings("unchecked")
	protected void encode(ChannelHandlerContext context, Packet<?> packet, ByteBuf output) throws Exception {
		int id = Packets.getId((Class<? extends Packet<?>>) packet.getClass());
		if (id == -1) {
			throw new IOException("Can not encode Packet" + packet.getClass().getSimpleName() + ", since it is not registered");
		} else {
			FriendlyByteBuffer buffer = new FriendlyByteBuffer(output);
			buffer.writeInt(id);
			try {
				int startSize = buffer.writerIndex();
				packet.encode(buffer);
				int size = buffer.writerIndex() - startSize;
				if (size > 8388608) {
					throw new IllegalArgumentException("Packet " + packet.getClass().getSimpleName() + " is too big, it should be less than 8388608, but it is " + size);
				}
			} catch (Exception e) {
				if (packet.skippable()) {
					throw new SkipPacketException(e);
				} else {
					throw e;
				}
			}

		}
	}

}
