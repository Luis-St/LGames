package net.luis.network.packet.server;

import net.luis.client.player.AbstractClientPlayer;
import net.luis.game.type.GameType;
import net.luis.game.type.GameTypes;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.listener.PacketGetter;
import net.luis.player.GameProfile;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-st
 *
 */

public class PlayGameRequestPacket implements ServerPacket {
	
	private final GameType<?, ?> gameType;
	private final List<GameProfile> profiles;
	
	public PlayGameRequestPacket(GameType<?, ?> gameType, List<AbstractClientPlayer> players) {
		this.gameType = gameType;
		this.profiles = players.stream().map(AbstractClientPlayer::getProfile).collect(Collectors.toList());
	}
	
	public PlayGameRequestPacket(FriendlyByteBuffer buffer) {
		this.gameType = GameTypes.fromName(buffer.readString());
		this.profiles = buffer.readList(() -> buffer.read(GameProfile.class));
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeString(this.gameType.getName());
		buffer.writeList(this.profiles, buffer::write);
	}
	
	@PacketGetter
	public GameType<?, ?> getGameType() {
		return this.gameType;
	}
	
	@PacketGetter
	public List<GameProfile> getProfiles() {
		return this.profiles;
	}
	
}
