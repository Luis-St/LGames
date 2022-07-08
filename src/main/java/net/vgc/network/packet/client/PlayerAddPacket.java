package net.vgc.network.packet.client;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.player.GameProfile;

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
	
	@Override
	public void handle(ClientPacketListener listener) {
		listener.handlePlayerAdd(this.profile);
	}
	
	public GameProfile getProfile() {
		return this.profile;
	}
	
}
