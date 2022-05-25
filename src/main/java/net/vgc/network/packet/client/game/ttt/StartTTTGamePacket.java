package net.vgc.network.packet.client.game.ttt;

import java.util.List;
import java.util.stream.Collectors;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.game.player.GamePlayerType;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.oldgame.ttt.TTTType;
import net.vgc.player.GameProfile;
import net.vgc.server.player.ServerPlayer;

public class StartTTTGamePacket implements ClientPacket {
	
	protected final TTTType playerType;
	protected final List<GameProfile> profiles;
	
	public StartTTTGamePacket(GamePlayerType playerType, List<ServerPlayer> players) {
		this.playerType = (TTTType) playerType;
		this.profiles = players.stream().map(ServerPlayer::getProfile).collect(Collectors.toList());
	}
	
	public StartTTTGamePacket(FriendlyByteBuffer buffer) {
		this.playerType = buffer.readEnum(TTTType.class);
		this.profiles = buffer.readList(buffer, (buf) -> {
			return buf.read(GameProfile.class);
		});
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeInt(this.playerType.getId());
		buffer.writeList(buffer, this.profiles, (buf, profile) -> {
			buf.write(profile);
		});
	}

	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleStartTTTGame(this.playerType, this.profiles);
	}
	
	public TTTType getPlayerType() {
		return this.playerType;
	}
	
	public List<GameProfile> getProfiles() {
		return this.profiles;
	}
	
}
