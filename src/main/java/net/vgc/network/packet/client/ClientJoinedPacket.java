package net.vgc.network.packet.client;

import java.util.List;
import java.util.stream.Collectors;

import net.vgc.client.network.ClientPacketHandler;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.player.GameProfile;
import net.vgc.server.player.ServerPlayer;

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
		this.profiles = buffer.readList(() -> {
			return buffer.read(GameProfile.class);
		});
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeList(this.profiles, (profile) -> {
			buffer.write(profile);
		});
	}
	
	@Override
	public void handle(ClientPacketHandler handler) {
		handler.handleClientJoined(this.profiles);
	}
	
	public List<GameProfile> getProfiles() {
		return this.profiles;
	}
	
}
