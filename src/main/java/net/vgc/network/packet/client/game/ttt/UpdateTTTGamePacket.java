package net.vgc.network.packet.client.game.ttt;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.oldgame.ttt.map.TTTMap;

public class UpdateTTTGamePacket implements ClientPacket {
	
	protected final TTTMap map;
	
	public UpdateTTTGamePacket(TTTMap map) {
		this.map = map;
	}
	
	public UpdateTTTGamePacket(FriendlyByteBuffer buffer) {
		this.map = buffer.read(TTTMap.class);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.map);
	}

	@Override
	public void handle(ClientPacketListener listener) {
		
	}
	
	public TTTMap getMap() {
		return this.map;
	}

}
