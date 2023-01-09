package net.luis.network.packet.client.game;

import net.luis.game.map.field.GameFieldInfo;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.client.ClientPacket;
import net.luis.network.packet.listener.PacketGetter;

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
