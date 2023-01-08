package net.vgc.network.packet.server;

import net.vgc.client.player.AbstractClientPlayer;
import net.vgc.game.type.GameType;
import net.vgc.game.type.GameTypes;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.listener.PacketGetter;
import net.vgc.player.GameProfile;

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
