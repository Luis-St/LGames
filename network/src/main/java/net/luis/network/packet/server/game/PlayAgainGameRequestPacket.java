package net.luis.network.packet.server.game;

import net.luis.network.buffer.Encodable;
import net.luis.network.buffer.EncodableObject;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.listener.PacketGetter;
import net.luis.network.packet.server.ServerPacket;

/**
 *
 * @author Luis-st
 *
 */

public class PlayAgainGameRequestPacket implements ServerPacket {
	
	private final EncodableObject profile;
	
	public PlayAgainGameRequestPacket(Encodable profile) {
		this.profile = new EncodableObject(profile);
	}
	
	public PlayAgainGameRequestPacket(FriendlyByteBuffer buffer) {
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
