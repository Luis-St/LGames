package net.vgc.network.packet.client.game;

import net.vgc.game.map.field.GameFieldInfo;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.network.packet.listener.PacketGetter;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class UpdateGameMapPacket implements ClientPacket {
	
	private final List<GameFieldInfo> fieldInfos;
	
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
	
	@PacketGetter
	public List<GameFieldInfo> getFieldInfos() {
		return this.fieldInfos;
	}
	
}
