package net.vgc.network.packet.client.game;

import net.vgc.client.network.ClientPacketHandler;
import net.vgc.game.player.GamePlayer;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.player.GameProfile;

/**
 *
 * @author Luis-st
 *
 */

public class CurrentPlayerUpdatePacket implements ClientPacket {
	
	private final GameProfile profile;
	
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
	public void handle(ClientPacketHandler handler) {
		handler.handleCurrentPlayerUpdate(this.profile);
	}
	
	public GameProfile getProfile() {
		return this.profile;
	}
	
}
