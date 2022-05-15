package net.vgc.network.packet.client.game;

import java.util.List;
import java.util.stream.Collectors;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.game.GamePlayerType;
import net.vgc.game.ttt.TTTType;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.player.GameProfile;
import net.vgc.server.player.ServerPlayer;

public class StartTTTGamePacket implements ClientPacket {
	
	protected final TTTType playerType;
	protected final GameProfile profile;
	protected final List<GameProfile> profiles;
	
	public StartTTTGamePacket(GamePlayerType playerType, ServerPlayer player, List<ServerPlayer> players) {
		this.playerType = (TTTType) playerType;
		this.profile = player.getProfile();
		this.profiles = players.stream().map(ServerPlayer::getProfile).collect(Collectors.toList());
	}
	
	public StartTTTGamePacket(FriendlyByteBuffer buffer) {
		this.playerType = buffer.readEnum(TTTType.class);
		this.profile = buffer.read(GameProfile.class);
		this.profiles = buffer.readList(buffer, (buf) -> {
			return buf.read(GameProfile.class);
		});
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeInt(this.playerType.getId());
		buffer.write(this.profile);
		buffer.writeList(buffer, this.profiles, (buf, profile) -> {
			buf.write(profile);
		});
	}

	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleStartTTTGame(this.playerType, this.profile, this.profiles);
	}
	
	public TTTType getPlayerType() {
		return this.playerType;
	}
	
	public GameProfile getProfile() {
		return this.profile;
	}
	
	public List<GameProfile> getProfiles() {
		return this.profiles;
	}
	
}
