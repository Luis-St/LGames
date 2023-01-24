package net.luis.network.packet.server.game.dice;

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

public class RollDiceRequestPacket implements ServerPacket {
	
	private final EncodableObject profile;
	
	public RollDiceRequestPacket(Encodable profile) {
		this.profile = new EncodableObject(profile);
	}
	
	public RollDiceRequestPacket(FriendlyByteBuffer buffer) {
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
