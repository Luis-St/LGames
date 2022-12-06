package net.vgc.network.packet.server.game.dice;

import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.player.GameProfile;
import net.vgc.server.network.ServerPacketHandler;

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

	@Override
	public void handle(ServerPacketHandler handler) {
		handler.handleRollDiceRequest(this.profile);
	}
	
	public GameProfile getProfile() {
		return this.profile;
	}
	
}
