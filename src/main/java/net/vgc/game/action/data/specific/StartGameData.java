package net.vgc.game.action.data.specific;

import java.util.List;

import net.vgc.game.action.data.ActionData;
import net.vgc.game.player.GamePlayerInfo;
import net.vgc.game.type.GameType;
import net.vgc.game.type.GameTypes;
import net.vgc.network.buffer.FriendlyByteBuffer;

public class StartGameData extends ActionData {
	
	private final GameType<?, ?> gameType;
	private final List<GamePlayerInfo> playerInfos;
	
	public StartGameData(GameType<?, ?> gameType, List<GamePlayerInfo> playerInfos) {
		super();
		this.gameType = gameType;
		this.playerInfos = playerInfos;
	}
	
	public StartGameData(FriendlyByteBuffer buffer) {
		super(buffer);
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
	
	public GameType<?, ?> getGameType() {
		return this.gameType;
	}
	
	public List<GamePlayerInfo> getPlayerInfos() {
		return this.playerInfos;
	}
	
}
