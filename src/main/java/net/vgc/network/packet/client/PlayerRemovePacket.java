package net.vgc.network.packet.client;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.network.FriendlyByteBuffer;
import net.vgc.player.GameProfile;

public class PlayerRemovePacket implements ClientPacket {
	
	protected final GameProfile gameProfile;
	
	public PlayerRemovePacket(GameProfile gameProfile) {
		this.gameProfile = gameProfile;
	}
	
	public PlayerRemovePacket(FriendlyByteBuffer buffer) {
		this.gameProfile = buffer.readGameProfile();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeGameProfile(this.gameProfile);
	}
	
	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleClientPlayerRemove(this.gameProfile);
	}
	
}
