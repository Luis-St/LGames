package net.luis.game.map.field;

import net.luis.game.player.game.GamePlayerType;
import net.luis.game.player.GameProfile;
import net.luis.network.annotation.DecodingConstructor;
import net.luis.network.buffer.Encodable;
import net.luis.network.buffer.EncodableEnum;
import net.luis.network.buffer.FriendlyByteBuffer;
import net.luis.utils.util.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class GameFieldInfo implements Encodable {
	
	private final EncodableEnum fieldType;
	private final EncodableEnum playerType;
	private final GameFieldPos fieldPos;
	private final GameProfile profile;
	private final int figureCount;
	private final UUID figureUUID;
	
	@SuppressWarnings("unchecked")
	public GameFieldInfo(@NotNull GameFieldType fieldType, @NotNull GamePlayerType playerType, @NotNull GameFieldPos fieldPos, @NotNull GameProfile profile, int figureCount, @NotNull UUID figureUUID) {
		this.fieldType = new EncodableEnum((Enum<? extends GameFieldType>) fieldType);
		this.playerType = new EncodableEnum((Enum<? extends GamePlayerType>) playerType);
		this.fieldPos = fieldPos;
		this.profile = profile;
		this.figureCount = figureCount;
		this.figureUUID = figureUUID;
	}
	
	@DecodingConstructor
	public GameFieldInfo(@NotNull FriendlyByteBuffer buffer) {
		this.fieldType = buffer.read(EncodableEnum.class);
		this.playerType = buffer.read(EncodableEnum.class);
		this.fieldPos = buffer.readInterface();
		GameProfile profile = buffer.read(GameProfile.class);
		this.profile = profile.equals(GameProfile.EMPTY) ? GameProfile.EMPTY : profile;
		this.figureCount = buffer.readInt();
		this.figureUUID = buffer.readUUID();
	}
	
	public @NotNull GameFieldType getFieldType() {
		return this.fieldType.getAsOrThrow(GameFieldType.class);
	}
	
	public @NotNull GamePlayerType getPlayerType() {
		return this.playerType.getAsOrThrow(GamePlayerType.class);
	}
	
	public @NotNull GameFieldPos getFieldPos() {
		return this.fieldPos;
	}
	
	public @NotNull GameProfile getProfile() {
		return this.profile;
	}
	
	public int getFigureCount() {
		return this.figureCount;
	}
	
	public @NotNull UUID getFigureUUID() {
		return this.figureUUID;
	}
	
	@Override
	public void encode(@NotNull FriendlyByteBuffer buffer) {
		buffer.write(this.fieldType);
		buffer.write(this.playerType);
		buffer.writeInterface(this.fieldPos);
		buffer.write(this.profile);
		buffer.writeInt(this.figureCount);
		buffer.writeUUID(this.figureUUID);
	}
	
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (!(o instanceof GameFieldInfo that)) return false;
		
		if (this.figureCount != that.figureCount) return false;
		if (!this.fieldType.equals(that.fieldType)) return false;
		if (!this.playerType.equals(that.playerType)) return false;
		if (!this.fieldPos.equals(that.fieldPos)) return false;
		if (!this.profile.equals(that.profile)) return false;
		return this.figureUUID.equals(that.figureUUID);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.fieldType, this.playerType, this.fieldPos, this.profile, this.figureCount, this.figureUUID);
	}
	
	@Override
	public @NotNull String toString() {
		return ToString.toString(this);
	}
	
}
