package net.vgc.network.packet.client;

import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.listener.PacketGetter;
import net.vgc.player.GameProfile;
import net.vgc.server.player.ServerPlayer;

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
