package net.vgc.network.packet.server.game;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.game.GameType;
import net.vgc.game.GameTypes;
import net.vgc.network.FriendlyByteBuffer;
import net.vgc.network.packet.server.ServerPacket;
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
		List<GameProfile> profiles = Lists.newArrayList();
		int index = buffer.readInt();
		for (int i = 0; i < index; i++) {
			profiles.add(buffer.readProfile());
		}
		this.profiles = profiles;
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeString(this.gameType.getName());
		buffer.writeInt(this.profiles.size());
		for (GameProfile profile : this.profiles) {
			buffer.writeProfile(profile);
		}
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
