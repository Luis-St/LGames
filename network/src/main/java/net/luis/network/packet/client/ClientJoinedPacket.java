package net.luis.network.packet.client;

import net.luis.network.buffer.Encodable;
import net.luis.network.buffer.EncodableObject;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.listener.PacketGetter;
import net.luis.utils.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class ClientJoinedPacket implements ClientPacket {
	
	private final List<EncodableObject> profiles;
	
	public ClientJoinedPacket(@NotNull List<? extends Encodable> profiles) {
		this.profiles = Utils.mapList(profiles, EncodableObject::new);
	}
	
	public ClientJoinedPacket(@NotNull FriendlyByteBuffer buffer) {
		this.profiles = buffer.readList(() -> buffer.read(EncodableObject.class));
	}
	
	@Override
	public void encode(@NotNull FriendlyByteBuffer buffer) {
		buffer.writeList(this.profiles, buffer::write);
	}
	
	@PacketGetter
	public @NotNull List<? extends Encodable> getProfiles() {
		return Utils.mapList(this.profiles, EncodableObject::get);
	}
	
}
