package net.vgc.network.packet.client;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.network.FriendlyByteBuffer;
import net.vgc.player.GameProfile;
import net.vgc.server.player.ServerPlayer;

public class ClientJoinedPacket implements ClientPacket {
	
	protected final List<GameProfile> gameProfiles;
	
	public ClientJoinedPacket(List<ServerPlayer> players) {
		this.gameProfiles = players.stream().map(ServerPlayer::getGameProfile).collect(Collectors.toList());
	}

	public ClientJoinedPacket(FriendlyByteBuffer buffer) {
		List<GameProfile> gameProfiles = Lists.newArrayList();
		int index = buffer.readInt();
		for (int i = 0; i < index; i++) {
			gameProfiles.add(buffer.readGameProfile());
		}
		this.gameProfiles = gameProfiles;
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeInt(this.gameProfiles.size());
		for (GameProfile gameProfile : this.gameProfiles) {
			buffer.writeGameProfile(gameProfile);
		}
	}

	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleClientJoined(this.gameProfiles);
	}
	
	public List<GameProfile> getGameProfiles() {
		return this.gameProfiles;
	}

}
