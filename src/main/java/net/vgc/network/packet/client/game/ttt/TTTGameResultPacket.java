package net.vgc.network.packet.client.game.ttt;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;
import net.vgc.oldgame.ttt.TTTType;
import net.vgc.oldgame.ttt.map.TTTResultLine;
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
		this.winnerProfile = buffer.read(GameProfile.class);
		this.winnerType = buffer.readEnum(TTTType.class);
		this.loserProfile = buffer.read(GameProfile.class);
		this.loserType = buffer.readEnum(TTTType.class);
		this.resultLine = buffer.read(TTTResultLine.class);
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.write(this.winnerProfile);
		buffer.writeEnum(this.winnerType);
		buffer.write(this.loserProfile);
		buffer.writeEnum(this.loserType);
		buffer.write(this.resultLine);
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
