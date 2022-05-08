package net.vgc.network.packet.client;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.network.FriendlyByteBuffer;
import net.vgc.player.GameProfile;

public class PlayerRemovePacket implements ClientPacket {
	
	protected final GameProfile profile;
	
	public PlayerRemovePacket(GameProfile profile) {
		this.profile = profile;
	}
	
	public PlayerRemovePacket(FriendlyByteBuffer buffer) {
		this.profile = buffer.readProfile();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeProfile(this.profile);
	}
	
	@Override
	public void handle(ClientPacketListener listener) {
		listener.handlePlayerRemove(this.profile);
	}
	
	public GameProfile getProfile() {
		return this.profile;
	}
	
}
