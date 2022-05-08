package net.vgc.network.packet.client.game;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.game.ttt.TTTType;
import net.vgc.network.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.player.GameProfile;
import net.vgc.server.player.ServerPlayer;

public class StartTTTGamePacket implements ClientPacket {
	
	protected final TTTType playerType;
	protected final GameProfile startPlayerGameProfile;
	protected final List<GameProfile> gameProfiles;
	
	public StartTTTGamePacket(TTTType playerType, ServerPlayer startPlayer, List<ServerPlayer> players) {
		this.playerType = playerType;
		this.startPlayerGameProfile = startPlayer.getGameProfile();
		this.gameProfiles = players.stream().map(ServerPlayer::getGameProfile).collect(Collectors.toList());
	}
	
	public StartTTTGamePacket(FriendlyByteBuffer buffer) {
		this.playerType = TTTType.fromId(buffer.readInt());
		this.startPlayerGameProfile = buffer.readGameProfile();
		List<GameProfile> gameProfiles = Lists.newArrayList();
		int index = buffer.readInt();
		for (int i = 0; i < index; i++) {
			gameProfiles.add(buffer.readGameProfile());
		}
		this.gameProfiles = gameProfiles;
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeInt(this.playerType.getId());
		buffer.writeGameProfile(this.startPlayerGameProfile);
		buffer.writeInt(this.gameProfiles.size());
		for (GameProfile gameProfile : this.gameProfiles) {
			buffer.writeGameProfile(gameProfile);
		}
	}

	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleStartTTTGame(this.playerType, this.startPlayerGameProfile, this.gameProfiles);
	}
	
	public TTTType getPlayerType() {
		return this.playerType;
	}
	
	public GameProfile getStartPlayerGameProfile() {
		return this.startPlayerGameProfile;
	}
	
	public List<GameProfile> getGameProfiles() {
		return this.gameProfiles;
	}
	
}
