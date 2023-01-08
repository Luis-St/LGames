package net.vgc.game.player;

import net.luis.utils.util.ToString;
import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.player.GameProfile;
import net.vgc.util.annotation.DecodingConstructor;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GamePlayerInfo that)) return false;
		
		if (!this.profile.equals(that.profile)) return false;
		if (!this.playerType.equals(that.playerType)) return false;
		return this.uuids.equals(that.uuids);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.profile, this.playerType, this.uuids);
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
}
