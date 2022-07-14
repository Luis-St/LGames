package net.vgc.network.packet.client.game;

import net.vgc.client.network.ClientPacketListener;
import net.vgc.game.GameResult;
import net.vgc.games.wins4.Wins4ResultLine;
import net.vgc.network.buffer.FriendlyByteBuffer;
import net.vgc.network.packet.client.ClientPacket;

public class Wins4GameResultPacket implements ClientPacket {
	
	private final GameResult result;
	private final Wins4ResultLine resultLine;
	
	public Wins4GameResultPacket(GameResult result, Wins4ResultLine resultLine) {
		this.result = result;
		this.resultLine = resultLine;
	}
	
	public Wins4GameResultPacket(FriendlyByteBuffer buffer) {
		this.result = buffer.readEnum(GameResult.class);
		Wins4ResultLine resultLine = buffer.read(Wins4ResultLine.class);
		this.resultLine = resultLine.equals(Wins4ResultLine.EMPTY) ? Wins4ResultLine.EMPTY : resultLine;
	}
	
	@Override
	public void encode(FriendlyByteBuffer buffer) {
		buffer.writeEnum(this.result);
		buffer.write(this.resultLine);
	}

	@Override
	public void handle(ClientPacketListener listener) {
		
	}
	
	public GameResult getResult() {
		return this.result;
	}
	
	public Wins4ResultLine getResultLine() {
		return this.resultLine;
	}

}
