package net.vgc.network.packet.client.game;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.game.ttt.TTTType;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.player.GameProfile;
import net.vgc.server.player.ServerPlayer;

public class StartTTTGamePacket implements ClientPacket {
	
	protected final TTTType playerType;
	protected final GameProfile startPlayerProfile;
	protected final List<GameProfile> profiles;
	
	public StartTTTGamePacket(TTTType playerType, ServerPlayer startPlayer, List<ServerPlayer> players) {
		this.playerType = playerType;
		this.startPlayerProfile = startPlayer.getProfile();
		this.profiles = players.stream().map(ServerPlayer::getProfile).collect(Collectors.toList());
	}
	
	public StartTTTGamePacket(FriendlyByteBuffer buffer) {
		this.playerType = TTTType.fromId(buffer.readInt());
		this.startPlayerProfile = buffer.readProfile();
		List<GameProfile> profiles = Lists.newArrayList();
		int index = buffer.readInt();
		for (int i = 0; i < index; i++) {
			profiles.add(buffer.readProfile());
		}
		this.profiles = profiles;
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeInt(this.playerType.getId());
		buffer.writeProfile(this.startPlayerProfile);
		buffer.writeInt(this.profiles.size());
		for (GameProfile profile : this.profiles) {
			buffer.writeProfile(profile);
		}
	}

	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleStartTTTGame(this.playerType, this.startPlayerProfile, this.profiles);
	}
	
	public TTTType getPlayerType() {
		return this.playerType;
	}
	
	public GameProfile getStartPlayerProfile() {
		return this.startPlayerProfile;
	}
	
	public List<GameProfile> getProfiles() {
		return this.profiles;
	}
	
}
