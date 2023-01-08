package net.vgc.game.map.field;

import net.luis.utils.util.ToString;
import net.vgc.game.player.GamePlayerType;
import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.player.GameProfile;
import net.vgc.util.annotation.DecodingConstructor;

import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Luis-st
 *
 */

public class GameFieldInfo implements Encodable {
	
	private final GameFieldType fieldType;
	private final GamePlayerType playerType;
	private final GameFieldPos fieldPos;
	private final GameProfile profile;
	private final int figureCount;
	private final UUID figureUUID;
	
	public GameFieldInfo(GameFieldType fieldType, GamePlayerType playerType, GameFieldPos fieldPos, GameProfile profile, int figureCount, UUID figureUUID) {
		this.fieldType = fieldType;
		this.playerType = playerType;
		this.fieldPos = fieldPos;
		this.profile = profile;
		this.figureCount = figureCount;
		this.figureUUID = figureUUID;
	}
	
	@DecodingConstructor
	public GameFieldInfo(FriendlyByteBuffer buffer) {
		this.fieldType = buffer.readEnumInterface();
		this.playerType = buffer.readEnumInterface();
		this.fieldPos = buffer.readInterface();
		GameProfile profile = buffer.read(GameProfile.class);
		this.profile = profile.equals(GameProfile.EMPTY) ? GameProfile.EMPTY : profile;
		this.figureCount = buffer.readInt();
		this.figureUUID = buffer.readUUID();
	}
	
	public GameFieldType getFieldType() {
		return this.fieldType;
	}
	
	public GamePlayerType getPlayerType() {
		return this.playerType;
	}
	
	public GameFieldPos getFieldPos() {
		return this.fieldPos;
	}
	
	public GameProfile getProfile() {
		return this.profile;
	}
	
	public int getFigureCount() {
		return this.figureCount;
	}
	
	public UUID getFigureUUID() {
		return this.figureUUID;
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeEnumInterface(this.fieldType);
		buffer.writeEnumInterface(this.playerType);
		buffer.writeInterface(this.fieldPos);
		buffer.write(this.profile);
		buffer.writeInt(this.figureCount);
		buffer.writeUUID(this.figureUUID);
	}
	
	@Override
	public boolean equals(Object o) {
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
	public String toString() {
		return ToString.toString(this);
	}
	
}
