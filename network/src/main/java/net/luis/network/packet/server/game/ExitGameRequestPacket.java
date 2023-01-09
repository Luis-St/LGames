package net.luis.network.packet.server.game;

import net.luis.common.player.GameProfile;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.listener.PacketGetter;
import net.luis.network.packet.server.ServerPacket;

/**
 *
 * @author Luis-st
 *
 */

public class ExitGameRequestPacket implements ServerPacket {
	
	private final GameProfile profile;
	
	public ExitGameRequestPacket(GameProfile profile) {
		this.profile = profile;
	}
	
	public ExitGameRequestPacket(FriendlyByteBuffer buffer) {
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
