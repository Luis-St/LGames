package net.vgc.network.packet.client.game;

import net.vgc.game.player.GamePlayerInfo;
import net.vgc.game.type.GameType;
import net.vgc.game.type.GameTypes;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.network.packet.listener.PacketGetter;

import java.util.List;

/**
 *
 * @author Luis-st
 *
 */

public class StartGamePacket implements ClientPacket {
	
	private final GameType<?, ?> gameType;
	private final List<GamePlayerInfo> playerInfos;
	
	public StartGamePacket(GameType<?, ?> gameType, List<GamePlayerInfo> playerInfos) {
		this.gameType = gameType;
		this.playerInfos = playerInfos;
	}
	
	public StartGamePacket(FriendlyByteBuffer buffer) {
		this.gameType = GameTypes.fromName(buffer.readString());
		this.playerInfos = buffer.readList(() -> buffer.read(GamePlayerInfo.class));
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeString(this.gameType.getName());
		buffer.writeList(this.playerInfos, buffer::write);
	}
	
	@PacketGetter
	public GameType<?, ?> getGameType() {
		return this.gameType;
	}
	
	@PacketGetter
	public List<GamePlayerInfo> getPlayerInfos() {
		return this.playerInfos;
	}
	
}
