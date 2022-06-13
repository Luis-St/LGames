package net.vgc.network.packet.server.game;

import net.vgc.game.map.field.GameFieldPos;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.player.GameProfile;
import net.vgc.server.network.ServerPacketListener;

public class SelectGameFieldPacket implements ServerPacket {
	
	protected final GameProfile profile;
	protected final GameFieldPos fieldPos;
	
	public SelectGameFieldPacket(GameProfile profile, GameFieldPos fieldPos) {
		this.profile = profile;
		this.fieldPos = fieldPos;
	}
	
	public SelectGameFieldPacket(FriendlyByteBuffer buffer) {
		this.profile = buffer.read(GameProfile.class);
		this.fieldPos = GameFieldPos.decode(buffer);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.profile);
		buffer.write(this.fieldPos);
	}

	@Override
	public void handle(ServerPacketListener listener) {
		
	}
	
	public GameProfile getProfile() {
		return this.profile;
	}
	
	public GameFieldPos getFieldPos() {
		return this.fieldPos;
	}

}
