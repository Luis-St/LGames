package net.luis.network.packet.client;

import net.luis.network.buffer.Encodable;
import net.luis.network.buffer.EncodableObject;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.listener.PacketGetter;
import net.luis.utils.util.Utils;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class ClientJoinedPacket implements ClientPacket {
	
	private final List<EncodableObject> profiles;
	
	public ClientJoinedPacket(List<? extends Encodable> profiles) {
		this.profiles = Utils.mapList(profiles, EncodableObject::new);
	}
	
	public ClientJoinedPacket(FriendlyByteBuffer buffer) {
		this.profiles = buffer.readList(() -> buffer.read(EncodableObject.class));
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeList(this.profiles, buffer::write);
	}
	
	@PacketGetter
	public List<? extends Encodable> getProfiles() {
		return Utils.mapList(this.profiles, EncodableObject::get);
	}
	
}
