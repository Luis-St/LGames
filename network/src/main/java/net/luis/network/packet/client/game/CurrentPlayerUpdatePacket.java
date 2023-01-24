package net.luis.network.packet.client.game;

import net.luis.network.buffer.Encodable;
import net.luis.network.buffer.EncodableObject;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.client.ClientPacket;
import net.luis.network.packet.listener.PacketGetter;

/**
 *
 * @author Luis-st
 *
 */

public class CurrentPlayerUpdatePacket implements ClientPacket {
	
	private final EncodableObject profile;
	
	public CurrentPlayerUpdatePacket(Encodable player) {
		this.profile = new EncodableObject(player);
	}
	
	public CurrentPlayerUpdatePacket(FriendlyByteBuffer buffer) {
		this.profile = buffer.read(EncodableObject.class);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.profile);
	}
	
	@PacketGetter
	public Encodable getProfile() {
		return this.profile.get();
	}
	
}
