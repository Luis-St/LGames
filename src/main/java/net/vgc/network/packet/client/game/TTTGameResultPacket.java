package net.vgc.network.packet.client.game;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.game.ttt.TTTType;
import net.vgc.game.ttt.map.TTTResultLine;
import net.vgc.network.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.player.GameProfile;

public class TTTGameResultPacket implements ClientPacket {
	
	protected final GameProfile winnerProfile;
	protected final TTTType winnerType;
	protected final GameProfile loserProfile;
	protected final TTTType loserType;
	protected final TTTResultLine resultLine;
	
	public TTTGameResultPacket(GameProfile winnerProfile, TTTType winnerType, GameProfile loserProfile, TTTType loserType, TTTResultLine resultLine) {
		this.winnerProfile = winnerProfile;
		this.winnerType = winnerType;
		this.loserProfile = loserProfile;
		this.loserType = loserType;
		this.resultLine = resultLine;
	}
	
	public TTTGameResultPacket(FriendlyByteBuffer buffer) {
		this.winnerProfile = buffer.readGameProfile();
		this.winnerType = TTTType.fromId(buffer.readInt());
		this.loserProfile = buffer.readGameProfile();
		this.loserType = TTTType.fromId(buffer.readInt());
		this.resultLine = buffer.readTTTResultLine();
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeGameProfile(this.winnerProfile);
		buffer.writeInt(this.winnerType.getId());
		buffer.writeGameProfile(this.loserProfile);
		buffer.writeInt(this.loserType.getId());
		buffer.writeTTTResultLine(this.resultLine);
	}

	@Override
	public void handle(ClientPacketListener listener) {
		
	}
	
	public GameProfile getWinnerProfile() {
		return this.winnerProfile;
	}
	
	public TTTType getWinnerType() {
		return this.winnerType;
	}
	
	public GameProfile getLoserProfile() {
		return this.loserProfile;
	}
	
	public TTTType getLoserType() {
		return this.loserType;
	}
	
	public TTTResultLine getResultLine() {
		return this.resultLine;
	}

}
