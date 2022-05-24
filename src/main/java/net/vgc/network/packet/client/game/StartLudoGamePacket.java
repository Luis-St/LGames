package net.vgc.network.packet.client.game;

import java.util.Map;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.game.GamePlayerType;
import net.vgc.game.ludo.LudoType;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.player.GameProfile;
import net.vgc.server.player.ServerPlayer;
import net.vgc.util.Util;

public class StartLudoGamePacket implements ClientPacket {
	
	protected final LudoType playerType;
	protected final Map<GameProfile, LudoType> playerTypes;
	
	public StartLudoGamePacket(GamePlayerType playerType, Map<ServerPlayer, LudoType> playerTypes) {
		this.playerType = (LudoType) playerType;
		this.playerTypes = Util.mapKey(playerTypes, ServerPlayer::getProfile);
	}
	
	public StartLudoGamePacket(FriendlyByteBuffer buffer) {
		this.playerType = buffer.readEnum(LudoType.class);
		this.playerTypes = buffer.readMap(buffer, (buf) -> {
			return buf.read(GameProfile.class);
		}, (buf) -> {
			return buf.readEnum(LudoType.class);
		});
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeEnum(this.playerType);
		buffer.writeMap(buffer, this.playerTypes, (buf, profile) -> {
			buf.write(profile);
		}, (buf, playerType) -> {
			buf.writeEnum(playerType);
		});
	}

	@Override
	public void handle(ClientPacketListener listener) {
		listener.handleStartLudoGame(this.playerType, this.playerTypes);
	}
	
	public LudoType getPlayerType() {
		return this.playerType;
	}
	
	public Map<GameProfile, LudoType> getPlayerTypes() {
		return this.playerTypes;
	}
	
}
