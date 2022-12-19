package net.vgc.network.packet.server.game;

import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.listener.PacketGetter;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.player.GameProfile;

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
