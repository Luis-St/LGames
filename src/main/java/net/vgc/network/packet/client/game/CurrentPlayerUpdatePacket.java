package net.vgc.network.packet.client.game;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.game.player.GamePlayer;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.player.GameProfile;

public class CurrentPlayerUpdatePacket implements ClientPacket {
	
	protected final GameProfile profile;
	
	public CurrentPlayerUpdatePacket(GamePlayer player) {
		this.profile = player.getPlayer().getProfile();
	}
	
	public CurrentPlayerUpdatePacket(FriendlyByteBuffer buffer) {
		this.profile = buffer.read(GameProfile.class);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.profile);
	}

	@Override
	public void handle(ClientPacketListener listener) {
		
	}
	
	public GameProfile getProfile() {
		return this.profile;
	}

}
