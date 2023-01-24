package net.luis.network.packet.client.game;

import net.luis.network.buffer.Encodable;
import net.luis.network.buffer.EncodableObject;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.client.ClientPacket;
import net.luis.network.packet.listener.PacketGetter;
import net.luis.utils.util.Utils;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class UpdateGameMapPacket implements ClientPacket {
	
	private final List<EncodableObject> fieldInfos;
	
	public UpdateGameMapPacket(List<Encodable> fieldInfos) {
		this.fieldInfos = Utils.mapList(fieldInfos, EncodableObject::new);
	}
	
	public UpdateGameMapPacket(FriendlyByteBuffer buffer) {
		this.fieldInfos = buffer.readList(() -> buffer.read(EncodableObject.class));
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeList(this.fieldInfos, buffer::write);
	}
	
	@PacketGetter
	public List<Encodable> getFieldInfos() {
		return Utils.mapList(this.fieldInfos, EncodableObject::get);
	}
	
}
