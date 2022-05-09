package net.vgc.network.packet.client.game;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.game.ttt.map.TTTMap;
import net.vgc.network.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.player.GameProfile;

public class UpdateTTTGamePacket implements ClientPacket {
	
	protected final TTTMap map;
	protected final GameProfile profile;
	
	public UpdateTTTGamePacket(TTTMap map, GameProfile profile) {
		this.map = map;
		this.profile = profile;
	}
	
	public UpdateTTTGamePacket(FriendlyByteBuffer buffer) {
		this.map = buffer.readTTTMap();
		this.profile = buffer.readProfile();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeTTTMap(this.map);
		buffer.writeProfile(this.profile);
	}

	@Override
	public void handle(ClientPacketListener listener) {
		
	}
	
	public TTTMap getMap() {
		return this.map;
	}
	
	public GameProfile getProfile() {
		return this.profile;
	}

}