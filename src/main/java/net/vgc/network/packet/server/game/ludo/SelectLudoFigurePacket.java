package net.vgc.network.packet.server.game.ludo;

import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.oldgame.ludo.map.field.LudoFieldPos;
import net.vgc.player.GameProfile;
import net.vgc.server.network.ServerPacketListener;

public class SelectLudoFigurePacket implements ServerPacket {
	
	protected final GameProfile profile;
	protected final LudoFieldPos pos;
	
	public SelectLudoFigurePacket(GameProfile profile, LudoFieldPos pos) {
		this.profile = profile;
		this.pos = pos;
	}
	
	public SelectLudoFigurePacket(FriendlyByteBuffer buffer) {
		this.profile = buffer.read(GameProfile.class);
		this.pos = buffer.read(LudoFieldPos.class);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.profile);
		buffer.write(this.pos);
	}

	@Override
	public void handle(ServerPacketListener listener) {
		listener.handleSelectLudoFigure(this.profile, this.pos);
	}
	
	public GameProfile getProfile() {
		return this.profile;
	}
	
	public LudoFieldPos getPos() {
		return this.pos;
	}

}
