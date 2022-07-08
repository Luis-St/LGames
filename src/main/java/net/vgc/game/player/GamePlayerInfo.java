package net.vgc.game.player;

import java.util.List;
import java.util.UUID;

import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.player.GameProfile;
import net.vgc.util.annotation.DecodingConstructor;

public class GamePlayerInfo implements Encodable {
	
	private final GameProfile profile;
	private final GamePlayerType playerType;
	private final List<UUID> uuids;
	
	public GamePlayerInfo(GameProfile profile, GamePlayerType playerType, List<UUID> uuids) {
		this.profile = profile;
		this.playerType = playerType;
		this.uuids = uuids;
	}
	
	@DecodingConstructor
	public GamePlayerInfo(FriendlyByteBuffer buffer) {
		GameProfile profile = buffer.read(GameProfile.class);
		this.profile = profile.equals(GameProfile.EMPTY) ? GameProfile.EMPTY : profile;
		this.playerType = buffer.readEnumInterface();
		this.uuids = buffer.readList(buffer::readUUID);
	}
	
	public GameProfile getProfile() {
		return this.profile;
	}
	
	public GamePlayerType getPlayerType() {
		return this.playerType;
	}
	
	public List<UUID> getUUIDs() {
		return this.uuids;
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.profile);
		buffer.writeEnumInterface(this.playerType);
		buffer.writeList(this.uuids, buffer::writeUUID);
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof GamePlayerInfo playerInfo) {
			if (!this.profile.equals(playerInfo.profile)) {
				return false;
			} else if (!this.playerType.equals(playerInfo.playerType)) {
				return false;
			} else {
				return this.uuids.equals(playerInfo.uuids);
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("GamePlayerInfo{");
		builder.append("profile=").append(this.profile).append(",");
		builder.append("playerType=").append(this.playerType).append(",");
		builder.append("uuids=").append(this.uuids).append("}");
		return builder.toString();
	}
	
}
