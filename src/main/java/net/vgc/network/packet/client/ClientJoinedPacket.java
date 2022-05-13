package net.vgc.network.packet.client;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.player.GameProfile;
import net.vgc.server.player.ServerPlayer;

public class ClientJoinedPacket implements ClientPacket {
	
	protected final List<GameProfile> profiles;
	
	public ClientJoinedPacket(List<ServerPlayer> players) {
		this.profiles = players.stream().map(ServerPlayer::getProfile).collect(Collectors.toList());
	}

	public ClientJoinedPacket(FriendlyByteBuffer buffer) {
		List<GameProfile> profiles = Lists.newArrayList();
		int index = buffer.readInt();
		for (int i = 0; i < index; i++) {
			profiles.add(buffer.readProfile());
		}
		this.profiles = profiles;
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeInt(this.profiles.size());
		for (GameProfile profile : this.profiles) {
			buffer.writeProfile(profile);
		}
	}

	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleClientJoined(this.profiles);
	}
	
	public List<GameProfile> getProfiles() {
		return this.profiles;
	}

}
