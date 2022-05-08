package net.vgc.network.packet.server.game;

import net.vgc.network.packet.server.ServerPacket;
import net.vgc.network.FriendlyByteBuffer;
import net.vgc.player.GameProfile;
import net.vgc.server.network.ServerPacketListener;

public class PressTTTFieldPacket implements ServerPacket {
	
	protected final GameProfile gameProfile;
	protected final int vMap;
	protected final int hMap;
	
	public PressTTTFieldPacket(GameProfile gameProfile, int vMap, int hMap) {
		this.gameProfile = gameProfile;
		this.vMap = vMap;
		this.hMap = hMap;
	}
	
	public PressTTTFieldPacket(FriendlyByteBuffer buffer) {
		this.gameProfile = buffer.readGameProfile();
		this.vMap = buffer.readInt();
		this.hMap = buffer.readInt();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeGameProfile(this.gameProfile);
		buffer.writeInt(this.vMap);
		buffer.writeInt(this.hMap);
	}

	@Override
	public void handle(ServerPacketListener listener) {
		listener.handlePressTTTField(this.gameProfile, this.vMap, this.hMap);
	}
	
	public GameProfile getGameProfile() {
		return this.gameProfile;
	}
	
	public int getVMap() {
		return this.vMap;
	}
	
	public int getHMap() {
		return this.hMap;
	}

}
