package net.vgc.network.packet.client.game;

import java.util.UUID;

import net.vgc.client.network.ClientPacketHandler;
import net.vgc.game.map.field.GameFieldPos;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.player.GameProfile;

/**
 *
 * @author Luis-st
 *
 */

public class UpdateGameFieldPacket implements ClientPacket {
	
	private final GameFieldPos fieldPos;
	private final GameProfile profile;
	private final int figureCount;
	private final UUID figureUUID;
	
	public UpdateGameFieldPacket(GameFieldPos fieldPos, GameProfile profile, int figureCount, UUID figureUUID) {
		this.fieldPos = fieldPos;
		this.profile = profile;
		this.figureCount = figureCount;
		this.figureUUID = figureUUID;
	}
	
	public UpdateGameFieldPacket(FriendlyByteBuffer buffer) {
		this.fieldPos = buffer.readInterface();
		this.profile = buffer.read(GameProfile.class);
		this.figureCount = buffer.readInt();
		this.figureUUID = buffer.readUUID();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeInterface(this.fieldPos);
		buffer.write(this.profile);
		buffer.writeInt(this.figureCount);
		buffer.writeUUID(this.figureUUID);
	}
	
	@Override
	public void handle(ClientPacketHandler handler) {
		
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
	
}
