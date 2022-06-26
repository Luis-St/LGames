package net.vgc.network.packet.client.game;

import java.util.List;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.game.map.field.GameFieldInfo;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;

public class UpdateGameMapPacket implements ClientPacket {
	
	protected final List<GameFieldInfo> fieldInfos;
	
	public UpdateGameMapPacket(List<GameFieldInfo> fieldInfos) {
		this.fieldInfos = fieldInfos;
	}
	
	public UpdateGameMapPacket(FriendlyByteBuffer buffer) {
		this.fieldInfos = buffer.readList(() -> {
			return buffer.read(GameFieldInfo.class);
		});
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeList(this.fieldInfos, buffer::write);
	}

	@Override
	public void handle(ClientPacketListener listener) {
		
	}
	
	public List<GameFieldInfo> getFieldInfos() {
		return this.fieldInfos;
	}

}
