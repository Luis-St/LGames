package net.luis.network.packet.client;

import net.luis.network.buffer.Encodable;
import net.luis.network.buffer.EncodableObject;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.listener.PacketGetter;

/**
 *
 * @author Luis-st
 *
 */

public class SyncPermissionPacket implements ClientPacket {
	
	private final EncodableObject profile;
	
	public SyncPermissionPacket(Encodable profile) {
		this.profile = new EncodableObject(profile);
	}
	
	public SyncPermissionPacket(FriendlyByteBuffer buffer) {
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
