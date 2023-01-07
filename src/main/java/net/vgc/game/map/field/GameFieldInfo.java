package net.vgc.game.map.field;

import net.vgc.game.player.GamePlayerType;
import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.player.GameProfile;
import net.vgc.util.annotation.DecodingConstructor;

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
	public boolean equals(Object object) {
		if (object instanceof GameFieldInfo fieldInfo) {
			if (!this.fieldType.equals(fieldInfo.fieldType)) {
				return false;
			} else if (!this.playerType.equals(fieldInfo.playerType)) {
				return false;
			} else if (!this.fieldPos.equals(fieldInfo.fieldPos)) {
				return false;
			} else if (!this.profile.equals(fieldInfo.profile)) {
				return false;
			} else if (this.figureCount != fieldInfo.figureCount) {
				return false;
			} else {
				return this.figureUUID.equals(fieldInfo.figureUUID);
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		String builder = "GameFieldInfo{" + "fieldType=" + this.fieldType + "," +
				"playerType=" + this.playerType + "," +
				"fieldPos=" + this.fieldPos + "," +
				"profile=" + this.profile + "," +
				"figureCount=" + this.figureCount + "," +
				"figureUUID=" + this.figureUUID + "}";
		return builder;
	}
	
}
