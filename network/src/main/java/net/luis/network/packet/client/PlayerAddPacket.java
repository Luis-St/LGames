package net.luis.network.packet.client;

import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.listener.PacketGetter;
import net.luis.player.GameProfile;

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
