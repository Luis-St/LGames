package net.vgc.network.packet.client.game;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.game.ttt.map.TTTMap;
import net.vgc.network.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.player.GameProfile;

public class UpdateTTTGamePacket implements ClientPacket {
	
	protected final TTTMap map;
	protected final GameProfile gameProfile;
	
	public UpdateTTTGamePacket(TTTMap map, GameProfile gameProfile) {
		this.map = map;
		this.gameProfile = gameProfile;
	}
	
	public UpdateTTTGamePacket(FriendlyByteBuffer buffer) {
		this.map = buffer.readTTTMap();
		this.gameProfile = buffer.readGameProfile();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeTTTMap(this.map);
		buffer.writeGameProfile(this.gameProfile);
	}

	@Override
	public void handle(ClientPacketListener listener) {
		
	}
	
	public TTTMap getMap() {
		return this.map;
	}
	
	public GameProfile getGameProfile() {
		return this.gameProfile;
	}

}
