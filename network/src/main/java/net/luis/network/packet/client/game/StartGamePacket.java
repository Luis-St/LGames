package net.luis.network.packet.client.game;

import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.client.ClientPacket;
import net.luis.network.packet.listener.PacketGetter;
import net.luis.game.player.GamePlayerInfo;
import net.luis.game.type.GameType;
import net.luis.game.type.GameTypes;

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
