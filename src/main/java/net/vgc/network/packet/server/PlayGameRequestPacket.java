package net.vgc.network.packet.server;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.game.GameType;
import net.vgc.game.GameTypes;
import net.vgc.network.FriendlyByteBuffer;
import net.vgc.player.GameProfile;
import net.vgc.server.network.ServerPacketListener;

public class PlayGameRequestPacket implements ServerPacket {
	
	protected final GameType<?> gameType;
	protected final List<GameProfile> gameProfiles;
	
	public PlayGameRequestPacket(GameType<?> gameType, List<AbstractClientPlayer> players) {
		this.gameType = gameType;
		this.gameProfiles = players.stream().map(AbstractClientPlayer::getGameProfile).collect(Collectors.toList());
	}
	
	public PlayGameRequestPacket(FriendlyByteBuffer buffer) {
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
	public void handle(ServerPacketListener listener) {
		listener.handlePlayGameRequest(this.gameType, this.gameProfiles);
	}

}
