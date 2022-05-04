package net.vgc.network.packet.client;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.network.FriendlyByteBuffer;
import net.vgc.player.GameProfile;

public class SyncPermissionPacket implements ClientPacket {
	
	protected final GameProfile gameProfile;
	
	public SyncPermissionPacket(GameProfile gameProfile) {
		this.gameProfile = gameProfile;
	}
	
	public SyncPermissionPacket(FriendlyByteBuffer buffer) {
		this.gameProfile = buffer.readGameProfile();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeGameProfile(this.gameProfile);
	}
	
	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleSyncPermission(this.gameProfile);
	}
	
	public GameProfile getGameProfile() {
		return this.gameProfile;
	}

}
