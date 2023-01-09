package net.luis.network.packet.server.game.dice;

import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.listener.PacketGetter;
import net.luis.network.packet.server.ServerPacket;
import net.luis.common.player.GameProfile;

/**
 *
 * @author Luis-st
 *
 */

public class RollDiceRequestPacket implements ServerPacket {
	
	private final GameProfile profile;
	
	public RollDiceRequestPacket(GameProfile profile) {
		this.profile = profile;
	}
	
	public RollDiceRequestPacket(FriendlyByteBuffer buffer) {
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
