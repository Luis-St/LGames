package net.vgc.network.packet.server;

import net.vgc.network.FriendlyByteBuffer;
import net.vgc.player.GameProfile;
import net.vgc.server.network.ServerPacketListener;

public class ExitGameRequestPacket implements ServerPacket {
	
	protected final GameProfile gameProfile;
	
	public ExitGameRequestPacket(GameProfile gameProfile) {
		this.gameProfile = gameProfile;
	}
	
	public ExitGameRequestPacket(FriendlyByteBuffer buffer) {
		this.gameProfile = buffer.readGameProfile();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeGameProfile(this.gameProfile);
	}

	@Override
	public void handle(ServerPacketListener listener) {
		listener.handleExitGameRequest(this.gameProfile);
	}

}
