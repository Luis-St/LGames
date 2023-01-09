package net.luis.network.packet.server.game;

import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.listener.PacketGetter;
import net.luis.network.packet.server.ServerPacket;
import net.luis.player.GameProfile;

/**
 *
 * @author Luis-st
 *
 */

public class PlayAgainGameRequestPacket implements ServerPacket {
	
	private final GameProfile profile;
	
	public PlayAgainGameRequestPacket(GameProfile profile) {
		this.profile = profile;
	}
	
	public PlayAgainGameRequestPacket(FriendlyByteBuffer buffer) {
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
