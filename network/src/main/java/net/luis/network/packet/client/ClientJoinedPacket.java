package net.luis.network.packet.client;

import net.luis.common.player.GameProfile;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.network.packet.listener.PacketGetter;
import net.luis.server.player.ServerPlayer;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-st
 *
 */

public class ClientJoinedPacket implements ClientPacket {
	
	private final List<GameProfile> profiles;
	
	public ClientJoinedPacket(List<ServerPlayer> players) {
		this.profiles = players.stream().map(ServerPlayer::getProfile).collect(Collectors.toList());
	}
	
	public ClientJoinedPacket(FriendlyByteBuffer buffer) {
		this.profiles = buffer.readList(() -> buffer.read(GameProfile.class));
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeList(this.profiles, buffer::write);
	}
	
	@PacketGetter
	public List<GameProfile> getProfiles() {
		return this.profiles;
	}
	
}
