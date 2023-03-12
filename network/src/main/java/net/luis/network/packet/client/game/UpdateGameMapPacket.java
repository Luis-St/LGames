package net.luis.network.packet.client.game;

import net.luis.network.buffer.Encodable;
import net.luis.network.buffer.EncodableObject;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.client.ClientPacket;
import net.luis.network.listener.PacketGetter;
import net.luis.utils.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class UpdateGameMapPacket implements ClientPacket {
	
	private final List<EncodableObject> fieldInfos;
	
	public UpdateGameMapPacket(@NotNull List<? extends Encodable> fieldInfos) {
		this.fieldInfos = Utils.mapList(fieldInfos, EncodableObject::new);
	}
	
	public UpdateGameMapPacket(@NotNull FriendlyByteBuffer buffer) {
		this.fieldInfos = buffer.readList(() -> buffer.read(EncodableObject.class));
	}
	
	@Override
	public void encode(@NotNull FriendlyByteBuffer buffer) {
		buffer.writeList(this.fieldInfos, buffer::write);
	}
	
	@PacketGetter
	public @NotNull List<? extends Encodable> getFieldInfos() {
		return Utils.mapList(this.fieldInfos, EncodableObject::get);
	}
	
}
