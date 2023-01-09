package net.luis.network.packet.client.game;

import net.luis.game.player.GamePlayer;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.client.ClientPacket;
import net.luis.network.packet.listener.PacketGetter;
import net.luis.player.GameProfile;

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
	
	@PacketGetter
	public GameProfile getProfile() {
		return this.profile;
	}
	
}
