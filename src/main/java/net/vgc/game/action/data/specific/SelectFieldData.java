package net.vgc.game.action.data.specific;

import net.vgc.game.action.data.GameActionData;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.game.map.field.GameFieldType;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.player.GameProfile;

/**
 *
 * @author Luis-st
 *
 */

public class SelectFieldData extends GameActionData {
	
	private final GameProfile profile;
	private final GameFieldType fieldType;
	private final GameFieldPos fieldPos;
	
	public SelectFieldData(GameProfile profile, GameFieldType fieldType, GameFieldPos fieldPos) {
		super();
		this.profile = profile;
		this.fieldType = fieldType;
		this.fieldPos = fieldPos;
	}
	
	public SelectFieldData(FriendlyByteBuffer buffer) {
		super(buffer);
		this.profile = buffer.read(GameProfile.class);
		this.fieldType = buffer.readEnumInterface();
		this.fieldPos = buffer.readInterface();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.profile);
		buffer.writeEnumInterface(this.fieldType);
		buffer.writeInterface(this.fieldPos);
	}
	
	public GameProfile getProfile() {
		return this.profile;
	}
	
	public GameFieldType getFieldType() {
		return this.fieldType;
	}
	
	public GameFieldPos getFieldPos() {
		return this.fieldPos;
	}
	
}
