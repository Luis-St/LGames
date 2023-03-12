package net.luis.game.player.game;

import net.luis.game.player.GameProfile;
import net.luis.network.annotation.DecodingConstructor;
import net.luis.network.buffer.Encodable;
import net.luis.network.buffer.EncodableEnum;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.utils.util.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	private final EncodableEnum playerType;
	private final List<UUID> uuids;
	
	@SuppressWarnings("unchecked")
	public GamePlayerInfo(@NotNull GameProfile profile, @NotNull GamePlayerType playerType, @NotNull List<UUID> uuids) {
		this.profile = profile;
		this.playerType = new EncodableEnum((Enum<? extends GamePlayerType>) playerType);
		this.uuids = uuids;
	}
	
	@DecodingConstructor
	public GamePlayerInfo(@NotNull FriendlyByteBuffer buffer) {
		GameProfile profile = buffer.read(GameProfile.class);
		this.profile = GameProfile.EMPTY.equals(profile) ? GameProfile.EMPTY : profile;
		this.playerType = buffer.read(EncodableEnum.class);
		this.uuids = buffer.readList(buffer::readUUID);
	}
	
	public @NotNull GameProfile getProfile() {
		return this.profile;
	}
	
	public @NotNull GamePlayerType getPlayerType() {
		return this.playerType.getAsOrThrow(GamePlayerType.class);
	}
	
	public @NotNull List<UUID> getUUIDs() {
		return this.uuids;
	}
	
	@Override
	public void encode(@NotNull FriendlyByteBuffer buffer) {
		buffer.write(this.profile);
		buffer.write(this.playerType);
		buffer.writeList(this.uuids, buffer::writeUUID);
	}
	
	@Override
	public boolean equals(@Nullable Object o) {
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
	public @NotNull String toString() {
		return ToString.toString(this);
	}
	
}
