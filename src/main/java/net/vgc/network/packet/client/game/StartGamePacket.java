package net.vgc.network.packet.client.game;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Table.Cell;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.game.GameType;
import net.vgc.game.GameTypes;
import net.vgc.game.player.GamePlayerType;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.player.GameProfile;
import net.vgc.util.SimpleCell;

public class StartGamePacket implements ClientPacket {
	
	protected final GameType<?, ?> gameType;
	protected final List<Cell<GameProfile, GamePlayerType, List<UUID>>> playerInfos;
	
	public StartGamePacket(GameType<?, ?> gameType, List<Cell<GameProfile, GamePlayerType, List<UUID>>> playerInfos) {
		this.gameType = gameType;
		this.playerInfos = playerInfos;
	}
	
	public StartGamePacket(FriendlyByteBuffer buffer) {
		this.gameType = GameTypes.fromName(buffer.readString());
		this.playerInfos = buffer.readList(buffer, (buf) -> {
			GameProfile profile = buf.read(GameProfile.class);
			GamePlayerType playerType = GamePlayerType.decode(buf);
			List<UUID> uuids = buf.readList(buffer, FriendlyByteBuffer::readUUID);
			return new SimpleCell<>(profile, playerType, uuids);
		});
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeString(this.gameType.getName());
		buffer.writeList(buffer, this.playerInfos, (buf, playerInfo) -> {
			buf.write(playerInfo.getRowKey());
			playerInfo.getColumnKey().encode(buf);
			buf.writeList(buf, playerInfo.getValue(), FriendlyByteBuffer::writeUUID);
		});
	}

	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleStartGame(this.gameType, this.playerInfos);
	}
	
	public GameType<?, ?> getGameType() {
		return this.gameType;
	}
	
	public List<Cell<GameProfile, GamePlayerType, List<UUID>>> getPlayerInfos() {
		return this.playerInfos;
	}
	
}
