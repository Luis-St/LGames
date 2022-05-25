package net.vgc.network.packet.server.game;

import java.util.List;
import java.util.stream.Collectors;

import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.server.ServerPacket;
import net.vgc.oldgame.GameType;
import net.vgc.oldgame.GameTypes;
import net.vgc.player.GameProfile;
import net.vgc.server.network.ServerPacketListener;

public class PlayGameRequestPacket implements ServerPacket {
	
	protected final GameType<?> gameType;
	protected final List<GameProfile> profiles;
	
	public PlayGameRequestPacket(GameType<?> gameType, List<AbstractClientPlayer> players) {
		this.gameType = gameType;
		this.profiles = players.stream().map(AbstractClientPlayer::getProfile).collect(Collectors.toList());
	}
	
	public PlayGameRequestPacket(FriendlyByteBuffer buffer) {
		this.gameType = GameTypes.fromName(buffer.readString());
		this.profiles = buffer.readList(buffer, (buf) -> {
			return buf.read(GameProfile.class);
		});
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeString(this.gameType.getName());
		buffer.writeList(buffer, this.profiles, (buf, profile) -> {
			buf.write(profile);
		});
	}

	@Override
	public void handle(ServerPacketListener listener) {
		listener.handlePlayGameRequest(this.gameType, this.profiles);
	}
	
	public GameType<?> getGameType() {
		return this.gameType;
	}
	
	public List<GameProfile> getProfiles() {
		return this.profiles;
	}

}
