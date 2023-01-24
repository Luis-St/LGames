package net.luis.network.packet.client.game;

import net.luis.network.buffer.Encodable;
import net.luis.network.buffer.EncodableObject;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.client.ClientPacket;
import net.luis.network.packet.listener.PacketGetter;
import net.luis.utils.util.Utils;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class StartGamePacket implements ClientPacket {
	
	private final int gameType;
	private final List<EncodableObject> playerInfos;
	
	public StartGamePacket(int gameType, List<? extends Encodable> playerInfos) {
		this.gameType = gameType;
		this.playerInfos = Utils.mapList(playerInfos, EncodableObject::new);
	}
	
	public StartGamePacket(FriendlyByteBuffer buffer) {
		this.gameType = buffer.readInt();
		this.playerInfos = buffer.readList(() -> buffer.read(EncodableObject.class));
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeInt(this.gameType);
		buffer.writeList(this.playerInfos, buffer::write);
	}
	
	@PacketGetter
	public int getGameType() {
		return this.gameType;
	}
	
	@PacketGetter
	public List<? extends Encodable> getPlayerInfos() {
		return Utils.mapList(this.playerInfos, EncodableObject::get);
	}
	
}
