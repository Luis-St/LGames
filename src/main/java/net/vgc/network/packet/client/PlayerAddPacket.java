package net.vgc.network.packet.client;

import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.listener.PacketGetter;
import net.vgc.player.GameProfile;

/**
 *
 * @author Luis-st
 *
 */

public class PlayerAddPacket implements ClientPacket {
	
	private final GameProfile profile;
	
	public PlayerAddPacket(GameProfile profile) {
		this.profile = profile;
	}
	
	public PlayerAddPacket(FriendlyByteBuffer buffer) {
		this.profile = buffer.read(GameProfile.class);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.profile);
	}
	
	@PacketGetter
	public GameProfile getProfile() {
		return this.profile;
	}
	
}
