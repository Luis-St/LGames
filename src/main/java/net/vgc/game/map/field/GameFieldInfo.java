package net.vgc.game.map.field;

import java.util.UUID;

import net.vgc.network.buffer.Encodable;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.player.GameProfile;
import net.vgc.util.annotation.DecodingConstructor;

public class GameFieldInfo implements Encodable {
	
	protected final GameFieldPos fieldPos;
	protected final GameProfile profile;
	protected final int figureCount;
	protected final UUID figureUUID;
	
	public GameFieldInfo(GameFieldPos fieldPos, GameProfile profile, int figureCount, UUID figureUUID) {
		this.fieldPos = fieldPos;
		this.profile = profile;
		this.figureCount = figureCount;
		this.figureUUID = figureUUID;
	}
	
	@DecodingConstructor
	private GameFieldInfo(FriendlyByteBuffer buffer) {
		this.fieldPos = GameFieldPos.decode(buffer);
		this.profile = buffer.read(GameProfile.class);
		this.figureCount = buffer.readInt();
		this.figureUUID = buffer.readUUID();
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
		buffer.write(this.fieldPos);
		buffer.write(this.profile);
		buffer.writeInt(this.figureCount);
		buffer.writeUUID(this.figureUUID);
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof GameFieldInfo fieldInfo) {
			if (!this.fieldPos.equals(fieldInfo.fieldPos)) {
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
		StringBuilder builder = new StringBuilder("GameFieldInfo{");
		builder.append("fieldPos=").append(this.fieldPos).append(",");
		builder.append("profile=").append(this.profile).append(",");
		builder.append("figureCount=").append(this.figureCount).append(",");
		builder.append("figureUUID=").append(this.figureUUID).append("}");
		return builder.toString();
	}

}
