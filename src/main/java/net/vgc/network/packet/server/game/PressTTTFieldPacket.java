package net.vgc.network.packet.server.game;

import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.player.GameProfile;
import net.vgc.server.network.ServerPacketListener;

public class PressTTTFieldPacket implements ServerPacket {
	
	protected final GameProfile profile;
	protected final int vMap;
	protected final int hMap;
	
	public PressTTTFieldPacket(GameProfile profile, int vMap, int hMap) {
		this.profile = profile;
		this.vMap = vMap;
		this.hMap = hMap;
	}
	
	public PressTTTFieldPacket(FriendlyByteBuffer buffer) {
		this.profile = buffer.readProfile();
		this.vMap = buffer.readInt();
		this.hMap = buffer.readInt();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeProfile(this.profile);
		buffer.writeInt(this.vMap);
		buffer.writeInt(this.hMap);
	}

	@Override
	public void handle(ServerPacketListener listener) {
		listener.handlePressTTTField(this.profile, this.vMap, this.hMap);
	}
	
	public GameProfile getProfile() {
		return this.profile;
	}
	
	public int getVMap() {
		return this.vMap;
	}
	
	public int getHMap() {
		return this.hMap;
	}

}
