package net.vgc.network.packet.client.game;

import java.util.List;

import net.vgc.client.network.ClientPacketHandler;
import net.vgc.game.player.GamePlayerInfo;
import net.vgc.game.type.GameType;
import net.vgc.game.type.GameTypes;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;

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
		this.playerInfos = buffer.readList(() -> {
			return buffer.read(GamePlayerInfo.class);
		});
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeString(this.gameType.getName());
		buffer.writeList(this.playerInfos, buffer::write);
	}
	
	@Override
	public void handle(ClientPacketHandler handler) {
		handler.handleStartGame(this.gameType, this.playerInfos);
	}
	
	public GameType<?, ?> getGameType() {
		return this.gameType;
	}
	
	public List<GamePlayerInfo> getPlayerInfos() {
		return this.playerInfos;
	}
	
}
