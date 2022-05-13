package net.vgc.network.packet.server.game;

import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.player.GameProfile;
import net.vgc.server.network.ServerPacketListener;

public class ExitGameRequestPacket implements ServerPacket {
	
	protected final GameProfile profile;
	
	public ExitGameRequestPacket(GameProfile profile) {
		this.profile = profile;
	}
	
	public ExitGameRequestPacket(FriendlyByteBuffer buffer) {
		this.profile = buffer.readProfile();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeProfile(this.profile);
	}

	@Override
	public void handle(ServerPacketListener listener) {
		listener.handleExitGameRequest(this.profile);
	}
	
	public GameProfile getProfile() {
		return this.profile;
	}

}
