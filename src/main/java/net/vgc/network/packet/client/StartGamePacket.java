package net.vgc.network.packet.client;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.game.GameType;
import net.vgc.game.GameTypes;
import net.vgc.network.FriendlyByteBuffer;
import net.vgc.player.GameProfile;
import net.vgc.server.player.ServerPlayer;

public class StartGamePacket implements ClientPacket {
	
	protected final GameType<?> gameType;
	protected final List<GameProfile> gameProfiles;
	
	public StartGamePacket(GameType<?> gameType, List<ServerPlayer> players) {
		this.gameType = gameType;
		this.gameProfiles = players.stream().map(ServerPlayer::getGameProfile).collect(Collectors.toList());
	}
	
	public StartGamePacket(FriendlyByteBuffer buffer) {
		this.gameType = GameTypes.fromName(buffer.readString());
		List<GameProfile> gameProfiles = Lists.newArrayList();
		int index = buffer.readInt();
		for (int i = 0; i < index; i++) {
			gameProfiles.add(buffer.readGameProfile());
		}
		this.gameProfiles = gameProfiles;
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeString(this.gameType.getName());
		buffer.writeInt(this.gameProfiles.size());
		for (GameProfile gameProfile : this.gameProfiles) {
			buffer.writeGameProfile(gameProfile);
		}
	}

	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleStartGame(this.gameType, this.gameProfiles);
	}
	
}
